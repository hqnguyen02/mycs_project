const {Activity} = require('./Activity')


class PlanActivity extends Activity {

    constructor(data) {
        data.name = data.activity_name;
        delete data.activity_name;
        
        super(data);

        this.time = 0;
        if(Activity.TIMED === this.type) {
            this.time = data.progress_estimate;
        }

        this.progressEstimate = Number.parseInt(data.progress_estimate);
    }
}

exports.PlanActivity = PlanActivity;