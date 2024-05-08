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
  Grid,
} from "@mui/material";
import { Edit, Delete } from "@mui/icons-material";

const modalStyle = {
  position: "absolute",
  top: "50%",
  left: "50%",
  transform: "translate(-50%, -50%)",
  width: 500, // Adjust width as needed
  bgcolor: "background.paper",
  boxShadow: 24,
  borderRadius: 2, // Rounded corners
  p: 4, // Padding
};

export const ExerciseTile = ({ exercise, onEdit, onDelete }) => {
  const [open, setOpen] = useState(false); // Modal for detailed info
  const [confirmDeleteOpen, setConfirmDeleteOpen] = useState(false); // Modal for delete confirmation

  const handleOpen = () => setOpen(true);
  const handleClose = () => setOpen(false);

  const openDeleteConfirmation = (e) => {
    e.stopPropagation();
    setConfirmDeleteOpen(true);
  };

  const closeDeleteConfirmation = () => setConfirmDeleteOpen(false);

  const handleDelete = async () => {
    await onDelete(exercise.exerciseId);
    closeDeleteConfirmation();
  };

  return (
    <>
      <Card sx={{ maxWidth: 345, margin: 2 }} onClick={handleOpen}>
        <CardMedia
          component="img"
          height="300"
          image={
            exercise.exerciseImageStartUrl ||
            "https://via.placeholder.com/140x140"
          }
          alt={exercise.name}
        />
        <CardContent>
          <Typography variant="h5" component="div">
            {exercise.name}
          </Typography>
        </CardContent>

        <CardActions>
          <Button
            size="small"
            color="secondary"
            startIcon={<Delete />}
            onClick={openDeleteConfirmation} // Opens the confirmation modal
          >
            Delete
          </Button>
        </CardActions>
      </Card>

      {/* Modal for Delete Confirmation */}
      <Modal open={confirmDeleteOpen} onClose={closeDeleteConfirmation}>
        <Box sx={modalStyle}>
          <Typography variant="h5" gutterBottom>
            Are you sure you want to delete the exercise "{exercise.name}"?
          </Typography>
          <Box
            sx={{
              display: "flex",
              justifyContent: "flex-end",
              gap: 2,
              mt: 2,
            }}
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

      {/* Existing Modal for Detailed Information */}
      <Modal open={open} onClose={handleClose}>
        <Box sx={modalStyle}>
          <Typography variant="h5" gutterBottom>
            {exercise.name}
          </Typography>
          <Grid container spacing={2}>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle1" color="text.primary">
                Start Image:
              </Typography>
              <CardMedia
                component="img"
                height="150"
                image={
                  exercise.exerciseImageStartUrl ||
                  "https://via.placeholder.com/150x150"
                }
                alt={`Start image of ${exercise.name}`}
              />
            </Grid>
            <Grid item xs={12} sm={6}>
              <Typography variant="subtitle1" color="text.primary">
                End Image:
              </Typography>
              <CardMedia
                component="img"
                height="150"
                image={
                  exercise.exerciseImageEndUrl ||
                  "https://via.placeholder.com/150x150"
                }
                alt={`End image of ${exercise.name}`}
              />
            </Grid>
          </Grid>
          <Typography variant="h6" color="text.primary" gutterBottom>
            Details
          </Typography>
          <Typography variant="body1" color="text.primary">
            Muscle Group: {exercise.muscleGroup.name}
          </Typography>

          <Typography variant="body1" color="text.primary">
            Equipment: {exercise.equipment.name}
          </Typography>

          <Typography variant="body1" color="text.primary">
            Difficulty Level: {exercise.difficulty.dificultyLevel}
          </Typography>

          <Typography variant="body1" color="text.primary">
            Category: {exercise.category.name}
          </Typography>

          <Typography variant="body1" color="text.primary">
            Description: {exercise.description || "No description available"}
          </Typography>
          <Box sx={{ textAlign: "right", mt: 2 }}>
            <Button onClick={handleClose} variant="outlined" color="secondary">
              Close
            </Button>
          </Box>
        </Box>
      </Modal>
    </>
  );
};
