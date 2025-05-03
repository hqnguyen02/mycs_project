const {PlanActivity} = require('./PlanActivity')


class ExercisePlan {
    constructor(data) {
        this.title = data.plan_title;
        this.user = data.user_id;

        this.activityData = [data];
    }

    addActivityData(activityData) {
        this.activityData = this.activityData.concat(activityData);
    }

    processData() {
        this.activities = this.activityData.map(value => {
            return new PlanActivity(value);
        });
    }
};

exports.ExercisePlan = ExercisePlan;