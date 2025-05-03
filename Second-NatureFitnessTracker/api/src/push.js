const webpush = require('web-push');

const subscriptions = new Map();

const vapidKeys = {
    publicKey: process.env.VAPID_PUBLIC_KEY,
    privateKey: process.env.VAPID_PRIVATE_KEY
}

webpush.setVapidDetails(process.env.VAPID_SUBJECT, vapidKeys.publicKey, vapidKeys.privateKey);

module.exports = {
    sendPush: (subscribedNameOrID, title, data) => {
        const subscription = module.exports.getSubscription(subscribedNameOrID);
        webpush.sendNotification(subscription, JSON.stringify({
            title: title,
            data: data
        }));
    },
    getSubscription: (subscribedNameOrID) => {
        const subscription = subscriptions.get(subscribedNameOrID);
        if(!subscription) {
            throw new Error("Failed to retrieve subscription");
        }

        return subscription;
    },
    setSubscription: (subscribedNameOrID, subscription) => {
        return subscriptions.set(subscribedNameOrID, subscription);
    }
}