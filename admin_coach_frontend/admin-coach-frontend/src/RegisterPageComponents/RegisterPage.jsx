import { useState } from "react";
import "./RegisterPage.css";
import { Link ,useNavigate } from "react-router-dom";
import axios from "axios";

export const Register = () => {
  const [email, setEmail] = useState("");
  const [password, setPassword] = useState("");
  const [repeatPassword, setRepeatPassword] = useState("");
  const [lastName, setLastName] = useState("");
  const [firstName, setFirstName] = useState("");
  const [showPasswordHint, setShowPasswordHint] = useState(false);

  const navigate = useNavigate();
  const onSubmit = async (event) => {
    event.preventDefault();

    if (password !== repeatPassword) {
      alert("Passwords do not match!");
      return;
    }
    try {
      const response = await axios.post("http://localhost:8080/api/v1/auth/register", {
        firstName,
        lastName,
        email,
        password,
        role: "COACH",
      });

      const token = response.data.access_token;

      localStorage.setItem('access_token', token);

      navigate('/coach-details');

    } catch (err) {
      console.error(err);
    }
  };

  return (

      <div className="register-container">
        <Form
          email={email}
          setEmail={setEmail}
          password={password}
          setPassword={setPassword}
          repeatPassword={repeatPassword}
          setRepeatPassword={setRepeatPassword}
          lastName={lastName}
          setLastName={setLastName}
          firstName={firstName}
          setFirstName={setFirstName}
          showPasswordHint={showPasswordHint}
          setShowPasswordHint={setShowPasswordHint}
          onSubmit={onSubmit}
        />
      </div>
    
  );
};

const Form = ({
  email,
  setEmail,
  password,
  setPassword,
  repeatPassword,
  setRepeatPassword,
  lastName,
  setLastName,
  firstName,
  setFirstName,
  showPasswordHint,
  setShowPasswordHint,
  onSubmit,
}) => {
  const handleGmailRegister = () => {};

  return (
    <div className="form-container">
      <form onSubmit={onSubmit} className="register-form">
        <h2 className="form-title">Register</h2>

        <div className="form-group">
          <input
            type="text"
            className="form-input"
            value={firstName}
            onChange={(event) => setFirstName(event.target.value)}
            required
            placeholder="First Name"
          />
        </div>
        <div className="form-group">
          <input
            type="text"
            className="form-input"
            value={lastName}
            onChange={(event) => setLastName(event.target.value)}
            required
            placeholder="Last Name"
          />
        </div>

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
            pattern="^(?=.*[a-z])(?=.*[A-Z])(?=.*\d)(?=.*[@$!%*?&])[A-Za-z\d@$!%*?&]{8,}$"
            onFocus={() => setShowPasswordHint(true)}
            onBlur={() => setShowPasswordHint(false)}
            placeholder="Password"
          />
        </div>
        {showPasswordHint && (
          <div className="password-hint">
            Password must be at least 8 characters long, include a number, an
            uppercase letter, and a special character.
          </div>
        )}
        <div className="form-group">
          <input
            type="password"
            className="form-input"
            value={repeatPassword}
            onChange={(event) => setRepeatPassword(event.target.value)}
            required
            placeholder="Password"
          />
        </div>
        <button type="submit" className="form-button">
          Register
        </button>

        <button
          type="button"
          className="form-button google-register"
          onClick={handleGmailRegister}
        >
          <i className="fab fa-google"></i> Register with Gmail
        </button>

        <p className="login-link">
          Already have an account? <Link to="/login">Log in</Link>
        </p>
      </form>
    </div>
  );
};
