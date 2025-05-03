// Import Express to create a router object
const express = require('express');
const cookieParser = require('cookie-parser');

const router = express.Router();

router.use(cookieParser());
router.use(express.json());

const { TokenMiddleware, generateToken, removeToken } = require('../middleware/TokenMiddleware');

const AuthDAO = require('../dao/AuthDAO');

router.post('/signin', (req,  res) => {
    if(req.body.username && req.body.password) {
        AuthDAO.getUserByCredentials(req.body.username, req.body.password).then(user => {
          generateToken(req, res, user);
    
          res.json({user: user});
        }).catch(err => {
          res.status(401).json({error: "Failed to authenticate the user"});
        });
      }
    else {
      res.status(400).json({error: 'Credentials not provided'});
    }
});

router.post('/signup', (req, res) => {
    const { username, password, email } = req.body;
    if (!username || !password || !email) {
      return res.status(400).json({ error: 'Username, password, and email are required' });
    }
    AuthDAO.signUpUser(username, password, email)
      .then(newUser => {
        res.status(200).json({ message: 'Sign-up successful', user: { id: newUser.id, username: newUser.username } });
      })
      .catch(err => {
          if (err.message.includes("already exists")) {
            res.status(409).json({ error: err.message }); // 409 Conflict
          } 
          else {
            res.status(500).json({ error: 'Failed to sign up: ' + err.message });
          }
      });
});

router.post('/logout', (req,  res) => {
    removeToken(req, res);
    res.json({success: true});
});
  
router.get('/users/current', TokenMiddleware, (req,  res) => {
    res.json(req.user);
});

exports.router = router;