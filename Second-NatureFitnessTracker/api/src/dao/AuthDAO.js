const UserDAO = require('./UserDAO');
const db = require('./SqlClient');
const crypto = require('crypto');
const User = require('../model/User'); 

module.exports = {
  getUserByCredentials: (username, password) => {
    return UserDAO.getUserByUsername(username).then(async user => {
      return await user.validatePassword(password);
    });
  },
  signUpUser: (username, password, email) => {
    return new Promise((resolve, reject) => {
      db.executeQuery('SELECT * FROM user WHERE usr_username = ? OR usr_email = ?', [username, email])
        .then(existingUsers => {
          if (existingUsers.length > 0) {
            reject(new Error("Username or email already exists."));
            return;
          }

          const salt = crypto.randomBytes(32).toString('hex');

          // 3. Hash Password
          crypto.pbkdf2(password, salt, 100000, 64, 'sha512', (err, derivedKey) => {
            if (err) {
              reject("Error hashing password: " + err);
              return;
            }
            const passwordHash = derivedKey.toString('hex');

            // 4. Insert User into Database
            db.executeStatement(
              'INSERT INTO user (usr_username, usr_email, usr_password, usr_salt) VALUES (?, ?, ?, ?)',
              [username, email, passwordHash, salt]
            )
            .then(result => {
              if (result.affectedRows === 1 && result.insertId > 0) {
                resolve({ success: true, message: "User account created successfully." });
              } 
              else {
                reject(new Error("Failed to create user account."));
              }
            })
            .catch(dbErr => {
              reject(new Error("Database error during sign up."));
            });
          });
        })
        .catch(checkErr => {
          reject(new Error("Database error in checking existing user."));
        });
    });
  }
};