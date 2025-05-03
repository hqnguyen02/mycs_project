class Activity {

    static TIMED = "TIMED";
    static REPEATED = "REPEATED";

    constructor(data) {
        this.name = data.name;
        this.type = data.is_timed ? Activity.TIMED : Activity.REPEATED;
    }
}

exports.Activity = Activity;