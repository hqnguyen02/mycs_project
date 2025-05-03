const { PlanActivity } = require('./PlanActivity');

class ActiveActivity extends PlanActivity {
    constructor(data) {
        super(data);
        this.progress = Number.parseInt(data.progress);
    }
}

module.exports = {
    ActiveActivity: ActiveActivity
}