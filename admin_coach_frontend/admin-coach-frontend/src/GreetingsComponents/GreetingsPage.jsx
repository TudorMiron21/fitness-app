import React, { useEffect, useState } from 'react';
import { useNavigate } from "react-router-dom";

import './GreetingsPage.css'; // Make sure you have your styles defined in this CSS file

export const GreetingsPage = () => {
  const [message, setMessage] = useState('');
  const fullMessage = "Geet Started With Your Coaching Adventure!"; // Corrected the typo in "Get"
  
  const navigate = useNavigate();

  useEffect(() => {
    let index = 0;
    const intervalId = setInterval(() => {
      setMessage((prev) => prev + fullMessage.charAt(index));
      index++;
      if (index >= fullMessage.length) {
        clearInterval(intervalId);
      }
    }, 75); // The speed of typing, in milliseconds

    return () => clearInterval(intervalId);
  }, [fullMessage]);

  const handleClickRegisterNow = ()=>
  {
    navigate('/register');
  }
  return (
    <div className="greetings-page">
      <div className="typing-message">
        {message}
      </div>
      <div className="register-button-container">
        <button className="register-button" onClick={handleClickRegisterNow}>
          Register now!
        </button>
      </div>
    </div>
  );
};