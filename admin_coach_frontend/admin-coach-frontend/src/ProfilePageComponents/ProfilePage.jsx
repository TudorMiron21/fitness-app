import React, { useState, useEffect } from "react";
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";

import {
  AppBar,
  Tabs,
  Tab,
  Box,
  Typography,
  Avatar,
  Toolbar,
} from "@mui/material";
import { WorkoutGrid } from "./WorkoutGrid"; // Import the grid component
import { ProgramGrid } from "./ProgramGrid";
import { ExerciseGrid } from "./ExerciseGrid";
import { CertificationGrid } from "./CertificationGrid";
import { NavBar } from "../NavBarComponents/NavBar";
export const ProfilePage = () => {
  const [value, setValue] = useState(0); // Start with the "Workouts" tab selected
  const navigate = useNavigate();

  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

  useEffect(() => {
    const checkTokenValid = async () => {
      try {
        const token = localStorage.getItem("access_token");

        if (token) {
          // setIsTokenValid(await validateToken(token));
          const isTokenValid = await validateToken(token);
          if (!isTokenValid) navigate("/login");
        } else navigate("/login");
      } catch (error) {
        console.error("Error fetching coach details status", error);
      }
    };
    checkTokenValid();
  }, []);
  return (
    <Box>
      <NavBar />
      <Box
        sx={{
          backgroundImage: `url('https://via.placeholder.com/1000x400')`,
          height: "400px",
        }}
      />
      <Box sx={{ position: "relative", top: "-50px", textAlign: "center" }}>
        <Avatar
          sx={{ width: "200px", height: "200px", border: "2px solid white" }}
          src="https://via.placeholder.com/200"
        />
        <Typography variant="h5">Coach Name</Typography>
        <Typography variant="body2">Coach's tagline or brief info</Typography>
      </Box>

      {/* Tabs */}
      <AppBar
        position="static"
        sx={{ backgroundColor: "#f5f5f5", color: "black" }}
      >
        <Toolbar>
          <Tabs
            value={value}
            onChange={handleChange}
            centered
            textColor="primary"
            indicatorColor="primary"
          >
            <Tab label="Exercises" />
            <Tab label="Workouts" />
            <Tab label="Programs" />
            <Tab label="Certifications" />
          </Tabs>
        </Toolbar>
      </AppBar>

      {/* Display Content Based on Tab */}
      <Box sx={{ padding: "20px" }}>
        {value === 0 && <ExerciseGrid />}
        {value === 1 && <WorkoutGrid />}
        {value === 2 && <ProgramGrid />}
        {value === 3 && <CertificationGrid />}
      </Box>
    </Box>
  );
};
