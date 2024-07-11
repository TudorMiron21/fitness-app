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

  if (!isTokenValid) {
    navigate("/login");
    return;
  } 
  else 
  {
    // Conditional rendering based on the 'isCoachDetailsValid' state
    if (areCoachDetailsValid === null) {
      return <Spinner />;
    } else if (areCoachDetailsValid) {
      return (
        <div>
          <NavBar />
          <div className="actions-container">
            <ActionCard
              title="Create Exercise"
              description="Define new exercises to include in workouts."
              onClick={handleCreateExercise}
              hoverContent="You can add a variety of exercises tailored to meet the specific needs of all your clients. Offering options across various categories such as muscle groups, equipment usage, and difficulty levels ensures comprehensive fitness customization. This approach empowers users with diverse choices to achieve their fitness goals effectively and enjoyably."
              backgroundImage={
                "https://e1.pxfuel.com/desktop-wallpaper/91/297/desktop-wallpaper-pose-muscle-muscle-rod-press-athlete-gym-bodybuilder-abs-gym-bodybuilder-gym-section-%D1%81%D0%BF%D0%BE%D1%80%D1%82-abs-workout.jpg"
              }
            />
            <ActionCard
              title="Create Workout"
              description="Build a workout session for your trainees."
              onClick={handleCreateWorkout}
              hoverContent="Enhance your app by integrating a diverse array of workout routines tailored to meet each client's unique needs. Utilize efficient filtering capabilities to swiftly add exercises to workouts, whether they are custom exercises or ones already available on the platform. This flexibility allows users to curate personalized workouts quickly and effectively, ensuring a seamless and customizable fitness experience."
              backgroundImage={
                "https://e1.pxfuel.com/desktop-wallpaper/271/312/desktop-wallpaper-man-gym-muscle-workout-sport-dumbbells-720x1280-bodybuilding-gym.jpg"
              }
            />
            <ActionCard
              title="Create Program"
              description="Design a comprehensive training program."
              onClick={handleCreateProgram}
              hoverContent="You can elevate your fitness program by incorporating a versatile array of workout routines that cater to individual needs and preferences. Utilize intuitive tools for seamlessly adding workouts, whether they are tailored exercises or those already available within the program. This flexibility empowers users to create personalized fitness plans efficiently, ensuring they can achieve their goals effectively while enjoying a tailored and engaging fitness experience.more content"
              backgroundImage={
                "https://e0.pxfuel.com/wallpapers/48/814/desktop-wallpaper-bodybuilder-world-s-strongest-man-workout-fitness-bodybuilding-cartoon.jpg"
              }
            />
          </div>
          {/* <Footer /> */}
        </div>
      );
    } else {
      return <WaitForApprovalPage />;
    }
  }
};
