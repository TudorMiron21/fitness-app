import React, { useState } from 'react';
import './NavBar.css'; // Make sure to create a corresponding CSS file

export const NavBar = () => {
  const [isNavExpanded, setIsNavExpanded] = useState(false);

  return (
    <nav className="navbar">
      <a href="/" className="nav-brand">FitStack</a>
      <button
        className="nav-toggle"
        aria-label="toggle navigation"
        onClick={() => setIsNavExpanded(!isNavExpanded)}
      >
        <span className="hamburger"></span>
      </button>
      <div className={isNavExpanded ? "nav-menu expanded" : "nav-menu"}>
        <ul>
          <li><a href="/home-page">Home Page</a></li>
          <li><a href="/chat">Chat</a></li>
          <li><a href="/profile">Profile</a></li>
          <li><a href="/forum">Forum</a></li>
        </ul>
      </div>
    </nav>
  );
};