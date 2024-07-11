import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { ExerciseTile } from "./ExerciseTile"; // Import the tile component

export const ExerciseGrid = () => {
  const [exercises, setExercises] = useState([]);
  const [loading, setLoading] = useState(true);

  const onDelete = async (exerciseId) => {
    if (!exerciseId || exerciseId === "undefined") {
      console.error("Invalid exercise ID");
      return;
    }
    try {
      const response = await axios.delete(
        `https://www.fit-stack.online/api/v1/adminCoachService/coach/deleteExercise/${exerciseId}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      setExercises((prevExercises) =>
        prevExercises.filter((exercise) => exercise.exerciseId !== exerciseId)
      );
    } catch (error) {
      console.error("Error deleting exercise:" + exerciseId, error);
    }
  };

  useEffect(() => {
    const fetchWorkouts = async () => {
      try {
        const response = await axios.get(
          "https://www.fit-stack.online/api/v1/adminCoachService/coach/getAllExercisesForCoach",
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
              <ExerciseTile
                exercise={exercise}
                onEdit={() => {}}
                onDelete={() => onDelete(exercise.exerciseId)}
              />{" "}
              {/* Render each workout as a tile */}
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
