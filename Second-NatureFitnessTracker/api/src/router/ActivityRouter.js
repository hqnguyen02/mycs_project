// Import Express to create a router object
const express = require('express');

const ActivityDAO = require('../dao/ActivityDAO');
const { TokenMiddleware } = require('../middleware/TokenMiddleware');

// Create a router for exercise plan routes
const router = express.Router();

router.use(TokenMiddleware);

router.get('/', (req,  res) => {
    ActivityDAO.getActivities().then(activities => {
        res.json({activities: activities});
    }).catch(error => {
        res.status(500).json({error: error});
    });
});
router.post('/', (req, res) => {

    /** Not yet implemented */
    ActivityDAO.createActivity(req.body);

    ActivityDAO.getActivities().then(activities => {
        res.json(activities[0]);
    }).catch(error => {
        res.status(500).json({error: error});
    });

})

router.get('/:activityId/metrics', (req, res) => {
    const activityId = req.params.activityId;
    const userId = req.user.id;

    ActivityDAO.getUserMetricsByActivity(userId, activityId).then(points => {
        res.json(points);
    }).catch(error => {
        res.status(500).json({error: error});
    })
});

exports.router = router;