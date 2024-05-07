import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { ProgramTile } from "./ProgramTile";

export const ProgramGrid = () => {
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);

  useEffect(() => {

    const fetchWorkouts = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/v1/adminCoachService/coach/getAllProgramsForCoach",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        );
        setPrograms(response.data);
      } catch (error) {
        console.error("Error fetching programs:", error);
      } finally {
        setLoading(false);
      }
    };

    fetchWorkouts();
  }, []);

  return (
    <div>
      {loading ? (
        <CircularProgress />
      ) : (
        <Grid container spacing={2} justifyContent="center">
          {programs.map((program, index) => (
            <Grid item key={index}>
              <ProgramTile
                program={program}
                onEdit={() => {}}
                onDelete={() => {}}
              />{" "}
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
