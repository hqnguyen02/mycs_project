const {ExercisePlan} = require('./ExercisePlan');
const {ActiveActivity} = require('./ActiveActivity')


class ActivePlan extends ExercisePlan {
    
    constructor(data) {
        super(data);
    
        this.startTimestamp = new Date(data.start_timestamp);
    }

    processData() {
        this.activities = this.activityData.map(value => {
            return new ActiveActivity(value);
        });
    }
}

module.exports = {
    ActivePlan: ActivePlan
};