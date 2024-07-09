import React, { useState, useEffect } from "react";
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";
import axios from "axios";
import { WaitForApprovalPage } from "../WaitForApprovalPageComponents/WaitForApprovalPage.jsx"; // Assuming this is in the same directory
import { Spinner } from "../SpinnerComponents/Spinner";

import image1 from "../assets/cover-profile1.jpg";
import image2 from "../assets/cover-profile2.jpg";
import image3 from "../assets/cover-profile3.jpg";

import {
  AppBar,
  Tabs,
  Tab,
  Box,
  Avatar,
  Toolbar,
  Button,
  Typography,
  CircularProgress,
} from "@mui/material";
import { WorkoutGrid } from "./WorkoutGrid"; // Import the grid component
import { ProgramGrid } from "./ProgramGrid";
import { ExerciseGrid } from "./ExerciseGrid";
import { CertificationGrid } from "./CertificationGrid";
import { NavBar } from "../NavBarComponents/NavBar";
export const ProfilePage = () => {
  const [value, setValue] = useState(0); // Start with the "Workouts" tab selected
  const navigate = useNavigate();
  const [selectedImage, setSelectedImage] = useState(
    "https://via.placeholder.com/200"
  );

  const [areCoachDetailsValid, setAreCoachDetailsValid] = useState(null);
  const [isTokenValid, setIsTokenValid] = useState(false);

  const [uploadError, setUploadError] = useState("");
  const [uploading, setUploading] = useState(false);

  useEffect(() => {
    const fetchImage = async () => {
      try {
        const response = await axios.get(
          "http://localhost:8080/api/selfCoach/user/getProfilePicture",
          {
            headers: {
              Authorization: `Bearer ${localStorage.getItem("access_token")}`,
            },
          }
        );

        if (response.status === 200) {
          setSelectedImage(response.data); 
          console.log(response.data);
        }
      } catch (error) {
        console.error("Error fetching image:", error);
      }
    };

    fetchImage();
  }, []);
  const uploadImage = async (file) => {
    setUploading(true);
    setUploadError("");

    const formData = new FormData();
    formData.append("file", file);

    try {
      await axios.put(
        "http://localhost:8080/api/selfCoach/user/uploadProfilePicture",
        formData,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      setUploading(false);
    } catch (error) {
      setUploading(false);
      setUploadError("Failed to upload image. Please try again.");
      console.error("Error uploading image:", error);
    }
  };
  const handleImageChange = (event) => {
    const file = event.target.files[0];
    if (file) {
      const imageUrl = URL.createObjectURL(file);
      setSelectedImage(imageUrl);
      uploadImage(file);
    }
  };
  const handleButtonClick = () => {
    document.getElementById("file-input").click();
  };
  const handleChange = (event, newValue) => {
    setValue(newValue);
  };

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
            "http://localhost:8080/api/v1/adminCoachService/coach/checkAreCoachDetailsValid",
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
  const imagePaths = [image1, image2, image3];

  const getRandomImagePath = () => {
    const randomIndex = Math.floor(Math.random() * imagePaths.length);
    return `url(${imagePaths[randomIndex]})`;
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
        <Box>
          <NavBar />
          <Box
            sx={{
              backgroundImage: getRandomImagePath(), // Dynamically set background image
              backgroundSize: "cover",
              backgroundPosition: "center",
              height: "400px",
            }}
          />
          <Box sx={{ position: "relative", top: "-50px", textAlign: "center" }}>
            <Avatar
              sx={{
                width: "200px",
                height: "200px",
                border: "2px solid white",
                margin: "0 auto",
                marginBottom: "20px",
              }}
              src={selectedImage}
            />
            <input
              id="file-input"
              type="file"
              accept="image/*"
              style={{ display: "none" }}
              onChange={handleImageChange}
            />
            <Button
              variant="contained"
              onClick={handleButtonClick}
              disabled={uploading}
            >
              Change Picture
            </Button>
            {uploading && <CircularProgress sx={{ mt: 2 }} />}
            {uploadError && (
              <Typography color="error" sx={{ mt: 2 }}>
                {uploadError}
              </Typography>
            )}
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
    
    } else {
      return <WaitForApprovalPage />;
    }
  }
};
