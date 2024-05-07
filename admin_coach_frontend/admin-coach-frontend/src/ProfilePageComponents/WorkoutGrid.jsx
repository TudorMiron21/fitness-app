import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { WorkoutTile } from "./WorkoutTile"; // Import the tile component

export const WorkoutGrid = () => {
  const [workouts, setWorkouts] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {
    // Fetch the workouts data from the server
    const fetchWorkouts = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/adminCoachService/coach/getAllWorkoutsForCoach",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        ); // API endpoint to fetch workouts
        setWorkouts(response.data);
      } catch (error) {
        console.error("Error fetching workouts:", error);
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
          {workouts.map((workout, index) => (
            <Grid item key={index}>
              <WorkoutTile
                workout={workout}
                onEdit={() => {}}
                onDelete={() => {}}
              />{" "}
              {/* Render each workout as a tile */}
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
