import HTTPClient from "./HTTPClient";

export default {
    getUserPlans: () => {
        return HTTPClient.get('/api/users/plans');
    },
    getPlan: (id) => {
        return HTTPClient.get(`/api/plans/${id}`);
    },
    createPlan: (plan) => {
        return HTTPClient.post(`/api/plans`, plan);
    },
    editPlan: (plan) => {
        return HTTPClient.put(`/api/plans`, plan); 
    },
    startPlan: (id) => {
        return HTTPClient.post('/api/plans/active', {
            planId: id
        });
    },
    endPlan: (activePlan) => {
        return HTTPClient.put('/api/plans/active/end', {
            activePlan: activePlan
        });
    },
    getActivePlan: () => {
        return HTTPClient.get('/api/plans/active');
    },
    logProgress: (activePlan, activityName, amount) => {


        return HTTPClient.put('/api/plans/active', {
            activePlan: activePlan,
            activityName: activityName,
            progress: amount
        });
    }
}