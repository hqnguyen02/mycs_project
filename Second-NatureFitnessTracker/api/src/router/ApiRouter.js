const express = require('express');
const router = express.Router();

const webpush = require('../push');

// Import the routes used for exercise plans
const exercisePlans = require('./PlanRouter');

// Import the routes used for activities
const activities = require('./ActivityRouter');

// Import the routes used for users
const user = require('./UserRouter');

// Import the routes used for login/logout functionalities
const log = require('./AuthRouter');
const { TokenMiddleware } = require('../middleware/TokenMiddleware');


router.use('/plans', exercisePlans.router);
router.use('/users', user.router);
router.use('/activities', activities.router);
router.use(log.router);

router.post('/subscribetopush', TokenMiddleware, (req, res) => {
    const userId = req.user.id;
    const subscription = req.body.subscription

    webpush.setSubscription(userId, subscription);
    res.json({subscription: subscription});
})


module.exports = router;