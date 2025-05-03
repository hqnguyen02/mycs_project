// Import Express to create a router object
const express = require('express');

const PlanDAO = require('../dao/PlanDAO');
const ActivePlanDAO = require('../dao/ActivePlanDAO');

const { TokenMiddleware } = require('../middleware/TokenMiddleware');
// Create a router for exercise plan routes
const router = express.Router();

router.use(TokenMiddleware);


router.get('/active', (req, res) => {
    const userId = req.user.id;

    ActivePlanDAO.getActivePlan(userId).then(activePlan => {
        if(!activePlan) {
            res.status(200).json({activePlan: undefined});
            return;
        }

        res.json({activePlan: activePlan});  
    }).catch(error => {
        res.status(500).json({error: error});
    });
});

router.put('/active', (req, res) => {
    const { activePlan, activityName, progress } = req.body;
    if(!activePlan || !activityName || !progress) {
        res.status(400).json({error: "Invalid request body"});
    }

    const userId = req.user.id;
    if(!activePlan.user || activePlan.user != userId) {
        res.status(403).json({error: "Not authorized to update this active plan session"});
        return;
    }

    ActivePlanDAO.putActivePlan(activePlan, activityName, progress).then(activePlan => {
        res.json(activePlan)
    }).catch(error => {       
        if(error.sqlMessage && -1 !== error.sqlMessage.search(/start_after_session/)) {
            res.status(400).json({ error: "Progress entered is inconsistent with the starting time of the session" });
            return;
        }

        res.status(500).json({ error: error });
    });
});

router.put('/active/end', (req, res) => {
    const activePlan = req.body.activePlan;
    if(!activePlan) {
        res.status(400).json({error: "Invalid request body"});
    }

    const userId = req.user.id;
    if(!activePlan.user || activePlan.user != userId) {
        res.status(403).json({error: "Not authorized to update this active plan session"});
        return;
    }

    ActivePlanDAO.endActiveSession(activePlan).then(() => {
        res.json({msg: "Active session has ended"});
    }).catch(error => {
        res.status(500).json({error: error});
    });
})

router.get('/:planId', (req, res) => {
    const planId = req.params.planId;
    const userId = req.user.id;

    PlanDAO.getPlan(planId, userId).then(plan => {
        res.json(plan);
    }).catch(error => {
        res.status(404).json(error.message);
    });
})

router.post('/active', async (req, res) => {
    const planId = req.body.planId;

    const userId = req.user.id;

    try {
        await ActivePlanDAO.addActivePlan(userId, planId);
        PlanDAO.findPlanById(planId).then(storedPlan => {
            res.json(storedPlan);
        }).catch(error => {
            res.status(404).json(error.message);
        });
    } catch(error) {
        res.status(500).json({error: error.message});
    }
})

router.post('/', (req, res) => {
    const plan = req.body
    const userId = req.user.id;

    if(userId !== plan.authorId) {
        res.status(403).json({error: "Not authorized to create an exercise plan on another user's behalf"});
    }

    PlanDAO.createPlan(plan).then(storedPlan => {
        res.json({plan: storedPlan});
    }).catch(error => {
        res.status(500).json({error: error});
    });
})

router.put('/', (req, res) => {
    const plan = req.body
    const userId = req.user.id;

    if(userId !== plan.authorId) {
        res.status(403).json({error: "Not authorized to create an exercise plan on another user's behalf"});
    }

    PlanDAO.editPlan(plan).then(storedPlan => {
        res.json({plan: storedPlan});
    }).catch(error => {
        res.status(500).json({error: error});
    });
})

exports.router = router;