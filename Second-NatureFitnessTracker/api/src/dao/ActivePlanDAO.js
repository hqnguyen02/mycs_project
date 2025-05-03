const { ActivePlan } = require('../model/ActivePlan');
const { Activity } = require('../model/Activity');


const SqlClient = require('./SqlClient');



function getActivePlansFromQuery(plan_activities) {
    // Group activities by plan name
    const exercisePlanPrimitives = plan_activities.map(plan_activity => new ActivePlan(plan_activity));
    const retrievedPlanMap = exercisePlanPrimitives.reduce((accumulator, currentValue) => {
        if(!accumulator.has(currentValue.title)) {
            accumulator.set(currentValue.title, currentValue);
        } else {
            accumulator.get(currentValue.title).addActivityData(currentValue.activityData);
        }

        return accumulator;
    }, new Map());

    const retrievedPlans = [];
    retrievedPlanMap.forEach(value => {

        value.processData();
        retrievedPlans.push(value);
    });

    return retrievedPlans;
}


module.exports = {
    getActivePlan: (userId) => {
        return SqlClient.executeQuery(`
            with active_plan as (
                select act.plan_title, act.start_timestamp, act.user_id, pa.activity_name, pa.progress_estimate, is_timed
                from active_session act, plan_activity pa, activity a
                where act.user_id = ?
                    and end_timestamp is null
                    and act.plan_title = pa.plan_title
                    and pa.activity_name = a.name
                    and start_timestamp = (
                        select max(start_timestamp)
                        from active_session act1
                        where act1.user_id = ?
                )
            )
            select ap.plan_title, ap.start_timestamp, ap.user_id, ap.activity_name, ap.progress_estimate, ap.is_timed, IF(sum(progress) is null, 0, sum(progress)) progress
            from active_plan ap LEFT JOIN session_log sl
                on ap.plan_title = sl.plan_title
                and ap.start_timestamp = sl.session_start_timestamp
                and ap.user_id = sl.user_id
                and ap.activity_name = sl.activity_name
            group by ap.plan_title, ap.start_timestamp, ap.user_id, ap.activity_name, ap.progress_estimate, ap.is_timed
        `, [userId, userId]).then(plan_activities => {

            const plans = getActivePlansFromQuery(plan_activities);
            return plans[0];
        })
    },
    addActivePlan: async (userId, planId) => {
        const activePlan = await module.exports.getActivePlan(userId);
        if(activePlan) {
            throw new Error("There is already an existing active exercise plan.");
        }

        return SqlClient.executeStatement(`
            INSERT INTO active_session VALUES 
	            (?, current_timestamp(), ?, NULL);    
        `, [planId, userId]);
    },
    putActivePlan: (activePlan, activityName, progress) => {
        const planTitle = activePlan.title;
        const sessionStartTimestamp = activePlan.startTimestamp;
        const userId = activePlan.user;

        const activity = activePlan.activities.find(activity => activity.name = activityName);
        if(!activity) {
            throw new Error("Invalid activity progress update");
        }

        let duration = 1;
        if(Activity.TIMED === activity.type) {
            duration = progress;
        }

        const SECONDS_TO_MILLI = 1000;

        const durationMilli = duration * SECONDS_TO_MILLI;

        const activityEndTimestamp = Date.now();
        const activityStartTimestamp = activityEndTimestamp - durationMilli;

        
        let activityStartTimestampStr = new Date(activityStartTimestamp).toISOString();
        activityStartTimestampStr = activityStartTimestampStr.slice(0, activityStartTimestampStr.indexOf('.'));

        let activityEndTimestampStr = new Date(activityEndTimestamp).toISOString();
        activityEndTimestampStr = activityEndTimestampStr.slice(0, activityEndTimestampStr.indexOf('.'));

        const promisePrevSetNumber = SqlClient.executeQuery(`
            select if(MAX(activity_set) is null, 0, MAX(activity_set)) prev_set_number
            from session_log
            where plan_title = ?
                and session_start_timestamp = ?
                and user_id = ?
                and activity_name = ?
        `, [planTitle, sessionStartTimestamp, userId, activityName]);

        return promisePrevSetNumber.then(rows => {
            if(0 === rows.length) {
                return 0;
            }

            return rows[0]['prev_set_number'];
        }).then(async setNumber => {
            let timestampStr = new Date(sessionStartTimestamp).toISOString();
            timestampStr = timestampStr.slice(0, timestampStr.indexOf('.'));

            await SqlClient.executeStatement(`
                INSERT INTO session_log (plan_title, session_start_timestamp, user_id, activity_name, activity_set, activity_start_timestamp, activity_end_timestamp, progress) VALUES
                    (?, ?, ?, ?, ?, ?, ?, ?)
            `, [
                planTitle, timestampStr, userId, activityName, setNumber + 1, activityStartTimestampStr, activityEndTimestampStr, progress
            ]); 

            return await module.exports.getActivePlan(userId);
        });
    },
    endActiveSession: (activePlan) => {
        const planTitle = activePlan.title;
        const userId = activePlan.user;
        const startTimestampStr = activePlan.startTimestamp.slice(0, activePlan.startTimestamp.indexOf('.'));

        return SqlClient.executeStatement(`
            UPDATE active_session 
            SET end_timestamp = current_timestamp()
            WHERE plan_title = ?
                and user_id = ?
                and start_timestamp = ?
        `, [planTitle, userId, startTimestampStr]);
    }
}