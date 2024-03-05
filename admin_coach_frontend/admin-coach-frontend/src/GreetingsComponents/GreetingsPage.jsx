import React, { useEffect, useState } from 'react';
import './GreetingsPage.css'; // Make sure you have your styles defined in this CSS file

export const GreetingsPage = () => {
  const [message, setMessage] = useState('');
  const fullMessage = "Geet Started With Your Coaching Adventure!"; // Corrected the typo in "Get"

  useEffect(() => {
    let index = 0;
    const intervalId = setInterval(() => {
      setMessage((prev) => prev + fullMessage.charAt(index));
      index++;
      if (index >= fullMessage.length) {
        clearInterval(intervalId);
      }
    }, 100); // The speed of typing, in milliseconds

    return () => clearInterval(intervalId);
  }, [fullMessage]);

  return (
    <div className="greetings-page">
      <div className="typing-message">
        {message}
      </div>
      <div className="register-button-container">
        <button className="register-button">
          Start Your Coaching Adventure
        </button>
      </div>
    </div>
  );
};