import React, { useState, useEffect } from "react";
import {
  Card,
  CardMedia,
  CardContent,
  Typography,
  CardActions,
  Button,
  Modal,
  Box,
  CircularProgress,
  Accordion,
  AccordionSummary,
  AccordionDetails,
  Grid,
} from "@mui/material";
import ExpandMoreIcon from "@mui/icons-material/ExpandMore";
import { Edit, Delete } from "@mui/icons-material";
import axios from "axios";

const modalStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 600,
  bgcolor: "background.paper",
  boxShadow: 24,
  p: 4,
  borderRadius: 2,
  overflowY: "auto",
  maxHeight: "80vh",
};

export const WorkoutTile = ({ workout, onEdit, onDelete }) => {
  const [open, setOpen] = useState(false);
  const [detailedWorkout, setDetailedWorkout] = useState(null);
  const [loading, setLoading] = useState(true);
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false);

  const openDeleteConfirmation = (e) => {
    e.stopPropagation();
    setConfirmDeleteOpen(true);
  };

  const closeDeleteConfirmation = () => setConfirmDeleteOpen(false);

  const handleOpen = async () => {
    await fetchDetailedWorkoutData();
    setOpen(true);
  };

  const handleClose = () => {
    setOpen(false);
    setLoading(true);
  };

  const handleDelete = async () => {
    await onDelete(workout.id);
    closeDeleteConfirmation();
  };
  const fetchDetailedWorkoutData = async () => {
    try {
      const response = await axios.get(
        `http://localhost:8080/api/v1/adminCoachService/coach/getDetailedWorkout/${workout.id}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      setDetailedWorkout(response.data);
    } catch (error) {
      console.error("Error fetching detailed workout:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <>
      <Card sx={{ maxWidth: 600, margin: 2 }} onClick={handleOpen}>
        <CardMedia
          component="img"
          height="300"
          image={workout.coverPhotoUrl || "https://via.placeholder.com/300x300"}
          alt={workout.name}
        />

        <CardContent>
          <Typography variant="h5">{workout.name}</Typography>
          <Typography variant="body2" color="text.secondary">
            Difficulty Level: {workout.difficultyLevel || "Unknown"}
          </Typography>
        </CardContent>

        <CardActions>
          {/* <Button
            size="small"
            color="primary"
            startIcon={<Edit />}
            onClick={(e) => {
              e.stopPropagation(); // Prevent card click from triggering
              onEdit(workout);
            }}
          >
            Edit
          </Button> */}
          <Button
            size="small"
            color="secondary"
            startIcon={<Delete />}
            onClick={openDeleteConfirmation}
          >
            Delete
          </Button>
        </CardActions>
      </Card>
      <Modal open={confirmDeleteOpen} onClose={closeDeleteConfirmation}>
        <Box sx={modalStyle}>
          <Typography variant="h5" gutterBottom>
            Are you sure you want to delete the workout "{workout.name}"?
          </Typography>
          <Box
            sx={{ display: "flex", justifyContent: "flex-end", gap: 2, mt: 2 }}
          >
            <Button
              onClick={closeDeleteConfirmation}
              variant="outlined"
              color="secondary"
            >
              Cancel
            </Button>
            <Button onClick={handleDelete} variant="contained" color="primary">
              Yes
            </Button>
          </Box>
        </Box>
      </Modal>
      <Modal open={open} onClose={handleClose}>
        <Box sx={modalStyle}>
          {loading ? (
            <CircularProgress /> // Show loading spinner while fetching
          ) : (
            <>
              <CardMedia
                component="img"
                height="300"
                image={
                  detailedWorkout.coverPhotoUrl ||
                  "https://via.placeholder.com/150x150"
                }
                alt={`Start image of ${detailedWorkout.name}`}
              />
              <Typography variant="h5">{detailedWorkout.name}</Typography>
              <Typography variant="h6">
                Description:{" "}
                {detailedWorkout.description || "No description available"}
              </Typography>
              <Typography variant="h6">
                Difficulty level: {detailedWorkout.difficultyLevel}
              </Typography>

              {/* Display exercises using Accordion */}
              {detailedWorkout.exercises.map((exercise) => (
                <Accordion key={exercise.exerciseId}>
                  <AccordionSummary expandIcon={<ExpandMoreIcon />}>
                    <Typography>{exercise.name}</Typography>
                  </AccordionSummary>
                  <AccordionDetails>
                    <Grid container spacing={2}>
                      <Grid item xs={12} sm={6}>
                        <Typography variant="subtitle1">
                          Start Image:
                        </Typography>
                        <CardMedia
                          component="img"
                          height="150"
                          image={
                            exercise.exerciseImageStartUrl ||
                            "https://via.placeholder.com/150x150"
                          }
                          alt={exercise.name}
                        />
                      </Grid>

                      <Grid item xs={12} sm={6}>
                        <Typography variant="subtitle1">End Image:</Typography>
                        <CardMedia
                          component="img"
                          height="150"
                          image={
                            exercise.exerciseImageEndUrl ||
                            "https://via.placeholder.com/150x150"
                          }
                          alt={exercise.name}
                        />
                      </Grid>

                      <Grid item xs={12}>
                        <Typography variant="body2">
                          Muscle Group: {exercise.muscleGroup.name}
                        </Typography>
                        <Typography variant="body2">
                          Equipment: {exercise.equipment.name}
                        </Typography>
                        <Typography variant="body2">
                          Difficulty Level: {exercise.difficulty.dificultyLevel}
                        </Typography>
                        <Typography variant="body2">
                          Category: {exercise.category.name}
                        </Typography>
                      </Grid>
                    </Grid>
                  </AccordionDetails>
                </Accordion>
              ))}

              <Button
                onClick={handleClose}
                variant="outlined"
                color="secondary"
                sx={{ mt: 2 }}
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
