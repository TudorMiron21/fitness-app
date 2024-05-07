import React, { useState } from "react";
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  CardActions,
  Button,
  Modal,
  Box,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  CircularProgress,
  Grid,
} from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import axios from "axios";

const modalStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600, // Adjust the width as needed
  bgcolor: "background.paper",
  boxShadow: 24,
  p: 4,
  borderRadius: 2,
  overflowY: "auto", // Enable scrolling if content overflows
  maxHeight: "80vh", // Max height to control modal size
};

export const ProgramTile = ({ program, onEdit, onDelete }) => {
  const [open, setOpen] = useState(false); // State to control modal visibility
  const [detailedProgram, setDetailedProgram] = useState(null); // State for detailed program data
  const [loading, setLoading] = useState(true); // Loading state for async operations

  const handleOpen = async () => {
    setLoading(true);
    await fetchDetailedProgramData(); // Fetch detailed data on open
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setLoading(true); // Reset loading state
  };

  const fetchDetailedProgramData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/adminCoachService/coach/getDetailedProgram/${program.id}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      setDetailedProgram(response.data); // Set detailed data
    } catch (error) {
      console.error("Error fetching detailed program:", error);
    } finally {
      setLoading(false); // End loading state
    }
  };

  return (
    <>
      {/* Program Card */}
      <Card sx={{ maxWidth: 345, margin: 2 }} onClick={handleOpen}>
        <CardMedia
          component="img"
          height="300"
          image={program.coverPhotoUrl || "https://via.placeholder.com/300x300"}
          alt={program.name}
        />

        <CardContent>
          <Typography variant="h5">{program.name}</Typography>
          <Typography variant="body2" color="text.secondary">
            Difficulty Level: {program.difficultyLevel || "Unknown"}
          </Typography>
        </CardContent>

        <CardActions>
          <Button
            size="small"
            color="primary"
            startIcon={<Edit />}
            onClick={(e) => {
              e.stopPropagation(); // Prevent card click from triggering
              onEdit(program);
            }}
          >
            Edit
          </Button>
          <Button
            size="small"
            color="secondary"
            startIcon={<Delete />}
            onClick={(e) => {
              e.stopPropagation(); // Prevent card click from triggering
              onDelete(program.id);
            }}
          >
            Delete
          </Button>
        </CardActions>
      </Card>

      {/* Modal for detailed program information */}
      <Modal open={open} onClose={handleClose}>
        <Box sx={modalStyle}>
          {loading ? (
            <CircularProgress /> // Loading indicator while fetching
          ) : (
            <>
              <CardMedia
                component="img"
                height="300"
                image={
                  detailedProgram.coverPhotoUrl ||
                  "https://via.placeholder.com/150x150"
                }
                alt={`Start image of ${detailedProgram.name}`}
              />
              <Typography variant="h5">{detailedProgram.name}</Typography>
              <Typography variant="body1" color="text.secondary">
                {detailedProgram.description || "No description provided."}
              </Typography>

              {/* Outer Accordion for Workout Programs */}
              {detailedProgram.workoutPrograms.map((workoutProgram) => (
                <Accordion key={workoutProgram.id}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography>
                      {" "}
                      Day {workoutProgram.workoutIndex}:
                      {workoutProgram.workout.name}
                    </Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    {/* Inner Accordion for Exercises */}
                    {workoutProgram.workout.exercises.map((exercise) => (
                      <Accordion key={exercise.id}>
                        <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                          <Typography>{exercise.name}</Typography>
                        </AccordionSummary>
                        <AccordionDetails>
                          <Typography variant="body1" color="text.primary">
                            Muscle Group: {exercise.muscleGroup.name}
                          </Typography>

                          <Typography variant="body1" color="text.primary">
                            Equipment: {exercise.equipment.name}
                          </Typography>

                          <Typography variant="body1" color="text.primary">
                            Difficulty Level:{" "}
                            {exercise.difficulty.dificultyLevel}
                          </Typography>

                          <Typography variant="body1" color="text.primary">
                            Category: {exercise.category.name}
                          </Typography>

                          <Typography variant="body1" color="text.primary">
                            Description:{" "}
                            {exercise.description || "No description available"}
                          </Typography>
                        </AccordionDetails>
                      </Accordion>
                    ))}
                  </AccordionDetails>
                </Accordion>
              ))}

              <Button
                onClick={handleClose}
                variant="outlined"
                color="secondary"
              >
                Close
              </Button>
            </>
          )}
        </Box>
      </Modal>
    </>
  );
};
