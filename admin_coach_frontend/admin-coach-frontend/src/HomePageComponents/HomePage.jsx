import React, { useState, useEffect } from "react";
import axios from "axios";
import { WaitForApprovalPage } from "../WaitForApprovalPageComponents/WaitForApprovalPage.jsx"; // Assuming this is in the same directory
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";

export const HomePage = () => {
  const [areCoachDetailsValid, setAreCoachDetailsValid] = useState(null);
  const [isTokenValid, setIsTokenValid] = useState(false);
  const navigate = useNavigate();

  // Fetch verification status when component mounts
  useEffect(() => {
    const checkCoachDetails = async () => {
      try {
        const token = localStorage.getItem("access_token");

        if (token) {
          setIsTokenValid(await validateToken(token));
        }

        const response = await axios.get(
          "http://localhost:8080/api/v1/adminCoachService/coach/checkAreCoachDetailsValid",
          {
            headers: {
              Authorization: `Bearer ${token}`,
            },
          }
        );
        setAreCoachDetailsValid(response.data);
      } catch (error) {
        console.error("Error fetching coach details status", error);
        // Handle the error state as needed, perhaps setting the state to false
        setAreCoachDetailsValid(false);
      }
    };

    checkCoachDetails();
  }, []);

  if (!isTokenValid) {
    navigate("/login");
    return;
  } else {
    // Conditional rendering based on the 'isCoachDetailsValid' state
    if (areCoachDetailsValid === null) {
      return <div>Loading...</div>; // Or some loading component/spinner
    } else if (areCoachDetailsValid) {
      return (
        <div>
          <h1>Greetings, Coach!</h1>
          <p>Welcome to your dashboard. Your details are valid.</p>
        </div>
      );
    } else {
      return <WaitForApprovalPage />;
    }
  }
};
