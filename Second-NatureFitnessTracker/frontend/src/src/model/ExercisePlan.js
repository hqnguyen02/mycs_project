export default class ExercisePlan {
    constructor(title) {
        this.title = title;
        this.activities = [];
    }

    setActivities(activities) {
        this.activities = activities;
    }
    
    computeTime() {

        let totalTime = 0;
        this.activities.forEach((activity) => totalTime = totalTime + activity.time);

        return convertTime(totalTime);
    }

    timeToString() {
        return formatTime(this.computeTime());
    }
};

export function convertTime(duration) {
    const SECONDS_PER_MINUTE = 60;

    return {
        minutes: Math.floor(duration / SECONDS_PER_MINUTE),
        seconds: duration % SECONDS_PER_MINUTE
    }
}

export function formatTime(time) {
    const {minutes, seconds} = time;

    let minuteString = `${minutes}`;
    if(minutes < 10) {
        minuteString = `0${minutes}`
    }

    let secondString = `${seconds}`;
    if(seconds < 10) {
        secondString = `0${seconds}`;
    }

    return `${minuteString}:${secondString}`;
}