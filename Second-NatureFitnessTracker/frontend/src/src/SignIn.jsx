import React, { useEffect, useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SignIn.css';
import AuthDAO from "./dao/AuthDAO";
import { Alert } from '@mui/material';
import { addItemOffline } from './db';
import OfflinePage from "./component/OfflinePage"
import { isOffline } from "./offline-util"

function SignIn() {
  const [offline, setOffline] = useState(false);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');

  const [attemptStatus, setAttemptStatus] = useState(undefined);

  const navigate = useNavigate();

  if(offline) {
    return <OfflinePage />
  }

  const handleSubmit = async (e) => {
    e.preventDefault();
    setAttemptStatus(undefined);

    console.log('Sign In attempted with:', { username, password });
    try {
      await AuthDAO.logIn(username, password);
      console.log('Login successful');
      // document.location.href = './'
      navigate('/');
    } catch (error) {
      setOffline(isOffline(error));

      console.error('Login failed:', error);
      setAttemptStatus({
        msg: "Failed to authenticate the user"
      });
    }
  };

  const handleSignUpRedirect = () => {
    navigate('/signup');
  };


  return (
    <div className="sign-in-container">
      <form class="sign-in-form" onSubmit={handleSubmit}>
        <h2>Sign In</h2>
        <div className="input-group">
          <label>Username</label>
          <input className = "sign-button"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter your username"
            required
          />
        </div>
        <div className="input-group">
          <label>Password</label>
          <input className = "sign-button"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
            required
          />
        </div>
        <button class="sign-in-button" type="submit">Sign In</button>
        <div className="button-divider">
          <span>or</span>
        </div>
        <button onClick={handleSignUpRedirect} className="sign-up-button-a">
          Sign Up
        </button>

        {attemptStatus && <Alert severity="error">{attemptStatus.msg}</Alert>}

      </form>
    </div>
  );
}

export default SignIn;