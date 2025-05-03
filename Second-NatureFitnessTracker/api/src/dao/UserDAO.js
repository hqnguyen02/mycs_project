const db = require('./SqlClient');
const User = require('../model/User');

module.exports = {
  getUserByUsername: (username) => {
    return db.executeQuery('SELECT * FROM user WHERE usr_username = ?', [username]).then(rows => {

        if (rows.length < 1) {
            // if no user with provided username
            const err = new Error("No such user");
            throw err;
        }

        const user = new User(rows[0]);
        return user;
    });

  }
};