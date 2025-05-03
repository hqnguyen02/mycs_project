const {ExercisePlan} = require('../model/ExercisePlan');
const SqlClient = require('./SqlClient');

const ActivityDAO = require('./ActivityDAO')



function getPlansFromQuery(plan_activities) {
    // Group activities by plan name
    const exercisePlanPrimitives = plan_activities.map(plan_activity => new ExercisePlan(plan_activity));
    const retrievedPlanMap = exercisePlanPrimitives.reduce((accumulator, currentValue) => {
        const key = `${currentValue.title}|${currentValue.user_id}|${currentValue.startTimestamp}`;

        if(!accumulator.has(key)) {
            accumulator.set(key, currentValue);
        } else {
            accumulator.get(key).addActivityData(currentValue.activityData);
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

const getPlans = () => {
    return SqlClient.executeQuery(`
            select * 
            from plan_activity pa, activity a
            where pa.activity_name = a.name
        `).then(plan_activities => {
            return getPlansFromQuery(plan_activities);
    })
}

module.exports = {
    getPlans: getPlans,
    getPlansByUser: (userId) => {
        return SqlClient.executeQuery(`
            select pa.plan_title, pa.activity_name, pa.progress_estimate, a.is_timed, uep.user_id
            from plan_activity pa, activity a, user_exercise_plan uep
            where pa.activity_name = a.name
                and uep.plan_title = pa.plan_title
                and uep.user_id = ?;
        `, [userId]).then(plan_activities => {
            return getPlansFromQuery(plan_activities);
    })
    },
    getPlan: (id, userId) => {
        return module.exports.getPlansByUser(userId).then(plans => {
            const plan = plans.find(plan => {
                return plan.title === id
            });

            if(!plan) {
                throw new Error('Plan not found');
            }

            return plan;
        })
    },
    findPlanById: (id) => {
        return getPlans().then(plans => {
            const plan = plans.find(plan => {
                return plan.title === id
            });

            if(!plan) {
                throw new Error('Plan not found');
            }

            return plan;
        })
    },
    createPlan: async (planData) => {
        const {title, authorId} = planData;

        const resultCreatePlan = await SqlClient.executeStatement(`
            INSERT INTO exercise_plan
                VALUES (?, ?)    
        `, [title, authorId]);
        if(0 === resultCreatePlan.affectedRows) {
            throw new Error("Failed to add exercise plan");
        }

        try {
            const resultSharePlan = await SqlClient.executeStatement(`
                INSERT INTO user_exercise_plan
                    VALUES (?, ?, ?)    
            `, [authorId, title, authorId]);
            if(0 === resultSharePlan.affectedRows) {
                throw new Error("Failed to give plan creator access to new exercise plan");
            }

            const activities = planData.activities;
            const promiseCreateActivities = activities.map(activity => {
                const {name, progressEstimate, numSets} = activity;
                const activityName = name;

                return SqlClient.executeStatement(`
                    INSERT INTO plan_activity
                        VALUES (?, ?, ?, ?, ?)
                `, [title, authorId, activityName, progressEstimate, numSets]);
            });

            await Promise.all(promiseCreateActivities);
        } catch(error) {
            console.log("Error while adding exercise plans. Reverting changes...");

            await SqlClient.executeStatement(`
                DELETE FROM plan_activity
                WHERE plan_title = ?
                    AND plan_author_id = ? 
            `, [title, authorId]);

            await SqlClient.executeStatement(`
                DELETE FROM user_exercise_plan
                WHERE plan_title = ?
                    AND plan_author_id = ? 
            `, [title, authorId]);

            await SqlClient.executeStatement(`
                DELETE FROM exercise_plan
                WHERE title = ?
                    AND author_id = ? 
            `, [title, authorId]);

            throw error;
        }
        
        const storedPlan = await module.exports.getPlan(title, authorId);
        return storedPlan;
    },
    editPlan: async (planEdited) => {
        const {title, authorId} = planEdited;

        const addedActivities = planEdited.addedActivities;
        const promiseAddActivities = addedActivities.map(activity => {
            const {name, progressEstimate, numSets} = activity;
            const activityName = name;

            return SqlClient.executeStatement(`
                INSERT INTO plan_activity
                    VALUES (?, ?, ?, ?, ?)
            `, [title, authorId, activityName, progressEstimate, numSets]);
        });        

        const storedActivities = planEdited.storedActivities;
        const promiseModifyActivities = storedActivities.map(activity => {
            const {name, progressEstimate} = activity;
            const activityName = name;

            return SqlClient.executeStatement(`
                UPDATE plan_activity
                SET progress_estimate = ?
                WHERE plan_title = ?
                    AND plan_author_id = ?
                    AND activity_name = ?
            `, [progressEstimate, title, authorId, activityName]);
        });

        
        await Promise.all(promiseAddActivities);
        await Promise.all(promiseModifyActivities);

        return await module.exports.getPlan(title, authorId);
    },
    sharePlan: (userId, planId, currentUser) => {
        return SqlClient.executeStatement(`
            INSERT INTO user_exercise_plan 
            SELECT ?, ep.title, ep.author_id FROM exercise_plan ep, user_exercise_plan uep
            WHERE ep.title = ?
            and ep.title = uep.plan_title
            and uep.user_id = ?; 
        `, [userId, planId, currentUser])
        .then(result => {
            if(0 === result.affectedRows) {
                return false;
            }

            return true;
        });
    }
}