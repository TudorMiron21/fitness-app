import React from 'react';
import './Footer.css'; 

export const Footer = () => {
  return (
    <footer className="site-footer">
      <div className="footer-content">
        <p>&copy; {new Date().getFullYear()} Your Company Name. All rights reserved.</p>
        <p>Follow us on:
          <a href="https://twitter.com" target="_blank" rel="noopener noreferrer">Twitter</a>,
          <a href="https://facebook.com" target="_blank" rel="noopener noreferrer">Facebook</a>,
          <a href="https://instagram.com" target="_blank" rel="noopener noreferrer">Instagram</a>
        </p>
        <p>
          <a href="/terms">Terms of Service</a> | 
          <a href="/privacy">Privacy Policy</a>
        </p>
      </div>
    </footer>
  );
};

