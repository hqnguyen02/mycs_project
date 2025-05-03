import HTTPClient from "./HTTPClient";

export default {

    getActivities: () => {
        return HTTPClient.get(`/api/activities`);
    },
    getUserMetricsByActivity: (name) => {
        return HTTPClient.get(`/api/activities/${name}/metrics`);
    }
}