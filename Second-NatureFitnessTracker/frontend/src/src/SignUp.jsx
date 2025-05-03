import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import './SignUp.css';
import AuthDAO from './dao/AuthDAO';
import OfflinePage from './component/OfflinePage';
import { isOffline } from './offline-util';

function SignUp() {
  const [offline, setOffline] = useState(false);

  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [email, setEmail] = useState('');
  const navigate = useNavigate();

  if(offline) {
    return <OfflinePage />
  }

  const handleSignUp = async (e) => {
    e.preventDefault();
    if (!username || !password || !email) {
      alert("Please fill in all fields.");
      return;
    }
    try {
      // Call the signUp function from AuthDAO
      await AuthDAO.signUp(username, password, email);
      alert("Sign up successful! Redirecting to login.");
      navigate('/signin');

    } catch (err) {
      setOffline(isOffline(err));

      console.error('Sign up failed:', err);
      alert("Sign up failed: " + err.message);
    }
  };

  return (
    <div className="sign-up-container">
      <form class="sign-up-form" onSubmit={handleSignUp}>
        <h2>Sign Up</h2>
        <div className="input-group">
          <label>Username</label>
          <input className = "sign-button"
            type="text"
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            placeholder="Enter your username"
          />
        </div>
        <div className="input-group">
          <label>Password</label>
          <input className = "sign-button"
            type="password"
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            placeholder="Enter your password"
          />
        </div>
        <div className="input-group">
          <label>Email</label>
          <input className = "sign-button"
            type="email"
            value={email}
            onChange={(e) => setEmail(e.target.value)}
            placeholder="Enter your email"
          />
        </div>
        <button class="sign-up-button-b" type="submit">Sign Up</button>
      </form>
    </div>
  );
}

export default SignUp;