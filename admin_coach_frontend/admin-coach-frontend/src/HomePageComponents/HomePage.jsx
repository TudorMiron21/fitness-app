import React, { useState, useEffect } from "react";
import axios from "axios";
import { WaitForApprovalPage } from "../WaitForApprovalPageComponents/WaitForApprovalPage.jsx"; // Assuming this is in the same directory
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar.jsx";
import { ActionCard } from "./ActionCard.jsx";
import "./HomePage.css";
import { Footer } from "../FooterComponent/Footer.jsx";
import { Spinner } from "../SpinnerComponents/Spinner.jsx";
export const HomePage = () => {
  const [areCoachDetailsValid, setAreCoachDetailsValid] = useState(null);
  const [isTokenValid, setIsTokenValid] = useState(false);
  const navigate = useNavigate();

  // Fetch verification status when component mounts
  useEffect(() => {
    const checkCoachDetails = async () => {
      try {
        const role = localStorage.getItem("role");

        const token = localStorage.getItem("access_token");

        if (token) {
          setIsTokenValid(await validateToken(token));
        }
        if (role === "ROLE_COACH") {
          const response = await axios.get(
            "https://www.fit-stack.online/api/v1/adminCoachService/coach/checkAreCoachDetailsValid",
            {
              headers: {
                Authorization: `Bearer ${token}`,
              },
            }
          );
          setAreCoachDetailsValid(response.data);
        } else if (role === "ROLE_ADMIN") {
          setAreCoachDetailsValid(true);
        }
      } catch (error) {
        console.error("Error fetching coach details status", error);
        // Handle the error state as needed, perhaps setting the state to false
        setAreCoachDetailsValid(false);
      }
    };

    checkCoachDetails();
  }, []);
  const handleCreateExercise = () => {
    navigate("/create-exercise-page");
  };

  const handleCreateWorkout = () => {
    navigate("/create-workout-page");
  };

  const handleCreateProgram = () => {
    navigate("/create-program-page");
  };

  const handleCreateChallenge = () => {
    // Implement the logic to navigate to the create challenge page or open a modal
  };

  if (!isTokenValid) {
    navigate("/login");
    return;
  } else {
    // Conditional rendering based on the 'isCoachDetailsValid' state
    if (areCoachDetailsValid === null) {
      return <Spinner />;
    } else if (areCoachDetailsValid) {
      return (
        <div>
          <NavBar />
          <h1>Greetings, Coach!</h1>
          <div className="actions-container">
            <ActionCard
              title="Create Exercise"
              description="Define new exercises to include in workouts."
              onClick={handleCreateExercise}
              hoverContent="more content"
              backgroundImage={
                "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg"
              }
            />
            <ActionCard
              title="Create Workout"
              description="Build a workout session for your trainees."
              onClick={handleCreateWorkout}
              hoverContent="more content"
              backgroundImage={
                "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg"
              }
            />
            <ActionCard
              title="Create Program"
              description="Design a comprehensive training program."
              onClick={handleCreateProgram}
              hoverContent="more content"
              backgroundImage={
                "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg"
              }
            />
            <ActionCard
              title="Create Challenge"
              description="Set up challenges to motivate your trainees."
              onClick={handleCreateChallenge}
              hoverContent="more content"
              backgroundImage={
                "https://www.simplilearn.com/ice9/free_resources_article_thumb/what_is_image_Processing.jpg"
              }
            />
          </div>
          <Footer />
        </div>
      );
    } else {
      return <WaitForApprovalPage />;
    }
  }
};
