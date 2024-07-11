import React, { useEffect, useState } from "react";
import { Grid, CircularProgress } from "@mui/material";
import axios from "axios";
import { ProgramTile } from "./ProgramTile";

export const ProgramGrid = () => {
  const [programs, setPrograms] = useState([]);
  const [loading, setLoading] = useState(true);

  const onDelete = async (programId) => {
    if (!programId || programId === "undefined") {
      console.error("Invalid exercise ID");
      return;
    }
    try {
      const response = await axios.delete(
        `https://www.fit-stack.online/api/v1/adminCoachService/coach/deleteProgram/${programId}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      setPrograms((prevPrograms) =>
        prevPrograms.filter((program) => program.id !== programId)
      );
    } catch (error) {
      console.error("Error deleting program:" + programId, error);
    }
  };
  useEffect(() => {

    const fetchWorkouts = async () => {
      try {
        const response = await axios.get(
          "https://www.fit-stack.online/api/v1/adminCoachService/coach/getAllProgramsForCoach",
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
                onDelete={() => onDelete(program.id)}
              />{" "}
            </Grid>
          ))}
        </Grid>
      )}
    </div>
  );
};
