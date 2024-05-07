import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { ExerciseTile } from "./ExerciseTile"; // Import the tile component

export const ExerciseGrid = () => {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const fetchWorkouts = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/adminCoachService/coach/getAllExercisesForCoach",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        ); // API endpoint to fetch workouts
        setExercises(response.data);
      } catch (error) {
        console.error("Error fetching exercises:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchWorkouts(); // Fetch data on component mount
  }, []);

  return (
    <div>
      {loading ? (
        <CircularProgress /> // Show loading spinner while data is being fetched
      ) : (
        <Grid container spacing={2} justifyContent="center">
          {exercises.map((exercise, index) => (
            <Grid item key={index}>
              <ExerciseTile exercise={exercise}                 onEdit={() => {}}
                onDelete={() => {}}/>{" "}
              {/* Render each workout as a tile */}
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
