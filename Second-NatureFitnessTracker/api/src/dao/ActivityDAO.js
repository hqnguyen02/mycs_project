const {Activity} = require('../model/Activity');
const SqlClient = require('./SqlClient');


module.exports = {
    getActivities: () => {
        return SqlClient.executeQuery(`
            select * from activity
        `).then(rows => {
            const activities = [];

            rows.forEach((activityData) => {
                const activity = new Activity(activityData);
                activities.push(activity);
            });

            return activities;
        });
    },
    getUserMetricsByActivity: (userId, activityName) => {

        return SqlClient.executeQuery(`
            select *, sum(progress) total_progress from (
                select activity_name, DATE(activity_start_timestamp) activity_start_date, progress
                from session_log
                where user_id = ?
                    and activity_name = ?
            ) activity_day_logs
            group by activity_name, activity_start_date;
        `, [userId, activityName]).then(rows => {

            const points = [];

            rows.forEach(daily_log => {
                const activityDate = new Date(daily_log.activity_start_date);
                const sum_progress = daily_log.total_progress;

                const point = {x: activityDate.toDateString(), y: sum_progress};
                points.push(point);
            })

            return points;
        });
    },
    createActivity: (activity) => {
        return true;
    }
}