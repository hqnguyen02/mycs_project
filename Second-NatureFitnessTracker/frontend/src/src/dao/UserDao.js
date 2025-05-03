import HTTPClient from "./HTTPClient"

export default {
    sharePlan: (username, exercisePlan) => {
        const payload = {}
        return HTTPClient.put(`/api/users/${username}/plans/${exercisePlan.title}`, payload);
    },
    subscribePush: (subscription) => {
        return HTTPClient.post('./api/subscribetopush', {subscription: subscription})
    }
}