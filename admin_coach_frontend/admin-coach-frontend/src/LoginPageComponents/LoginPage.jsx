import { useState } from "react";
import "./LoginPage.css";
import { Link } from 'react-router-dom'; 


export const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const onSubmit = (event) => {
    event.preventDefault();
    // Your login logic goes here
  };

  return (
    <div className="login-container">
      <Form
        email={email}
        setEmail={setEmail}
        password={password}
        setPassword={setPassword}
        onSubmit={onSubmit}
      />
    </div>
  );
};

const Form = ({ email, setEmail, password, setPassword, onSubmit }) => {
  return (
    <div className="form-container">
      <form onSubmit={onSubmit} className="login-form">
        <h2 className="form-title">Login</h2>
        <div className="form-group">
          <input
            type="email"
            className="form-input"
            value={email}
            onChange={(event) => setEmail(event.target.value)}
            required
            placeholder="Email"
          />
        </div>
        <div className="form-group">
          <input
            type="password"
            className="form-input"
            value={password}
            onChange={(event) => setPassword(event.target.value)}
            required
            placeholder="Password"
          />
        </div>
        <button type="submit" className="form-button">
          Log In
        </button>

        <p className="login-link">
          Don't have an account? <Link to="/register">Register now</Link>
        </p>

      </form>
    </div>
  );
};