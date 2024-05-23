import { useState } from "react";
import "./LoginPage.css";
import { Link, useNavigate } from "react-router-dom";
import axios from "axios";
import { jwtDecode } from "jwt-decode";

export const Login = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");

  const navigate = useNavigate();

  const onSubmit = async (event) => {
    event.preventDefault();

    try {
      const response = await axios.post(
        "https://fit-stack.online/api/v1/auth/login",
        {
          email,
          password,
        }
      );
      const token = response.data.access_token;
      console.log(token);
      localStorage.setItem("access_token", token);

      const decoded = jwtDecode(token);

      const authoritiesString = decoded.authorities;
      console.log(authoritiesString);

      const authoritiesArray = authoritiesString.slice(1, -1).split(", ");

      const authorities = authoritiesArray.map((role) => role.trim());

      console.log(authorities);
      if (authorities.includes("ROLE_ADMIN")) {
        localStorage.setItem("role", "ROLE_ADMIN");
        navigate("/home-page");
      } else if (authorities.includes("ROLE_COACH")) {
        localStorage.setItem("role", "ROLE_COACH");
        navigate("/home-page");
      } else {
        alert("Unauthorized: You do not have access to this area.");
      }
    } catch (err) {
      console.log(err);
      alert("You don't have an account");
    }
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
