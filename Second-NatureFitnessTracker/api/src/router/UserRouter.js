// Import Express to create a router object
const express = require('express');
const PlanDAO = require('../dao/PlanDAO');
const UserDAO = require('../dao/UserDAO');
const webpush = require('../push');

// Create a router for exercise plan routes
const router = express.Router();
const { TokenMiddleware } = require('../middleware/TokenMiddleware');

router.use(TokenMiddleware);

router.put('/:userId/plans/:planId', async (req, res) => {
    const username = req.params.userId;
    const planId = req.params.planId;
    const userId = req.user.id;

    const targetUserId = await UserDAO.getUserByUsername(username).then(targetUser => {
        return targetUser.id;
    });

    PlanDAO.sharePlan(targetUserId, planId, userId).then(success => {
        if(!success) {
            throw new Error("Failed to share the exercise plan");
        }

        try {
            webpush.sendPush(targetUserId, 'Plan Shared', 'Plan has been shared');
        } catch(error) {
            console.log(error);
        }

        res.json("Successfully shared the exercise plan");
    }).catch(error => {
        res.status(400).json(error.message);
    });
})

router.get('/plans', (req, res) => {
    const userId = req.user.id;

    PlanDAO.getPlansByUser(userId).then(plans => {
        res.json({plans: plans});
    });
})

exports.router = router;