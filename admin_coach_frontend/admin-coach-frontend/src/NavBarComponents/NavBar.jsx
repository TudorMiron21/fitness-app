import React, { useState, useEffect } from "react";
import "./NavBar.css"; // Make sure to create a corresponding CSS file

export const NavBar = () => {
  const [isNavExpanded, setIsNavExpanded] = useState(false);
  const [userRole, setUserRole] = useState("");
  useEffect(() => {
    const role = localStorage.getItem("role");
    if (role) {
      setUserRole(role);
    }
  }, []);
  return (
    <nav className="navbar">
      <a href="/" className="nav-brand">
        FitStack
      </a>
      <button
        className="nav-toggle"
        aria-label="toggle navigation"
        onClick={() => setIsNavExpanded(!isNavExpanded)}
      >
        <span className="hamburger"></span>
      </button>
      <div className={isNavExpanded ? "nav-menu expanded" : "nav-menu"}>
        <ul>
          <li>
            <a href="/home-page">Home Page</a>
          </li>
          <li>
            <a href="/chat-page">Chat</a>
          </li>
          <li>
            <a href="/profile-page">Profile</a>
          </li>

          {userRole === "ROLE_ADMIN" && (
            <li>
              <a href="/certifications">Certifications</a>
            </li>
          )}
        </ul>
      </div>
    </nav>
  );
};
