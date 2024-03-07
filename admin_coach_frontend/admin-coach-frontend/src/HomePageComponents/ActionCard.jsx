// ActionCard.jsx
import React from 'react';
import './ActionCard.css';

export const ActionCard = ({ title, description, onClick, hoverContent, backgroundImage }) => {
  return (
    <div
      className="action-card"
      onClick={onClick}
      style={{ backgroundImage: `url(${backgroundImage})` }}
    >
      <div className="card-content">
        <h3>{title}</h3>
        <p>{description}</p>
      </div>
      <div className="card-hover-content">
        {hoverContent}
      </div>
    </div>
  );
};