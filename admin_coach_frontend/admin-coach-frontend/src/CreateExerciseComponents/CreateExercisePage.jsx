import React, { useState } from "react";
import "./CreateExercisePage.css";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";

export const CreateExercise = () => {
  // State for the form fields
  const [exercise, setExercise] = useState({
    name: "",
    description: "",
    muscleGroup: "",
    category: "",
    difficulty: "",
    equipment: "",
    photos: {
      before: null,
      after: null,
    },
    video: null,
  });

  const [imagePreviews, setImagePreviews] = useState({
    before: null,
    after: null,
  });
  const [videoPreview, setVideoPreview] = useState(null);

  // Handlers for the form inputs
  const handleInputChange = (event) => {
    const { name, value } = event.target;
    setExercise({ ...exercise, [name]: value });
  };

  const handlePhotoChange = (event) => {
    const { name, files } = event.target;
    const file = files[0];
    setExercise({
      ...exercise,
      photos: { ...exercise.photos, [name]: file },
    });

    // Create and update image preview URL
    if (file) {
      const previewUrl = URL.createObjectURL(file);
      setImagePreviews({ ...imagePreviews, [name]: previewUrl });
    }
  };

  const handleVideoChange = (event) => {
    const file = event.target.files[0];
    setExercise({ ...exercise, video: file });

    // Create and update video preview URL
    if (file) {
      const previewUrl = URL.createObjectURL(file);
      setVideoPreview(previewUrl);
    }
  };

  const revokePreviewUrls = () => {
    if (imagePreviews.before) URL.revokeObjectURL(imagePreviews.before);
    if (imagePreviews.after) URL.revokeObjectURL(imagePreviews.after);
    if (videoPreview) URL.revokeObjectURL(videoPreview);
  };

  const handleSubmit = (event) => {
    event.preventDefault();
    // Revoke URLs to avoid memory leaks
    revokePreviewUrls();
    // Handle the submission...
    console.log(exercise);
  };

  return (
    <div>
      <NavBar />
      <form className="exercise-form" onSubmit={handleSubmit}>
        <label htmlFor="name">Exercise Name:</label>
        <input
          type="text"
          id="name"
          name="name"
          value={exercise.name}
          onChange={handleInputChange}
          required
        />

        <label htmlFor="description">Description:</label>
        <textarea
          id="description"
          name="description"
          value={exercise.description}
          onChange={handleInputChange}
          required
        ></textarea>

        <label htmlFor="muscleGroup">Muscle Group:</label>
        <select
          type="text"
          id="muscleGroup"
          name="muscleGroup"
          value={exercise.muscleGroup}
          onChange={handleInputChange}
          required
        >
          <option value="">Select difficulty</option>
          <option value="Category1">Beginner</option>
          <option value="Category2">Intermediate</option>
          <option value="Category3">Advanced</option>
        </select>

        <label htmlFor="category">Category:</label>
        <select
          type="text"
          id="category"
          name="category"
          value={exercise.category}
          onChange={handleInputChange}
          required
        >
          <option value="">Select difficulty</option>
          <option value="Category1">Beginner</option>
          <option value="Category2">Intermediate</option>
          <option value="Category3">Advanced</option>
        </select>

        <label htmlFor="difficulty">Difficulty Level:</label>
        <select
          id="difficulty"
          name="difficulty"
          value={exercise.difficulty}
          onChange={handleInputChange}
          required
        >
          <option value="">Select difficulty</option>
          <option value="Beginner">Beginner</option>
          <option value="Intermediate">Intermediate</option>
          <option value="Advanced">Advanced</option>
        </select>

        <label htmlFor="equipment">Equipment Required:</label>
        <select
          type="text"
          id="equipment"
          name="equipment"
          value={exercise.equipment}
          onChange={handleInputChange}
          required
        >
          <option value="">Select difficulty</option>
          <option value="1">Beginner</option>
          <option value="2">Intermediate</option>
          <option value="3">Advanced</option>
        </select>

        <label htmlFor="photos-before">Before Photo:</label>
        <input
          type="file"
          id="photos-before"
          name="before"
          onChange={handlePhotoChange}
          accept="image/*"
        />
        {imagePreviews.before && (
          <div>
            <label>Before Photo Preview:</label>
            <img
              src={imagePreviews.before}
              alt="Before Preview"
              style={{ width: "100%", height: "auto" }}
            />
          </div>
        )}

        <label htmlFor="photos-after">After Photo:</label>
        <input
          type="file"
          id="photos-after"
          name="after"
          onChange={handlePhotoChange}
          accept="image/*"
        />

        {imagePreviews.after && (
          <div>
            <label>After Photo Preview:</label>
            <img
              src={imagePreviews.after}
              alt="After Preview"
              style={{ width: "100%", height: "auto" }}
            />
          </div>
        )}
        <label htmlFor="video">Video:</label>
        <input
          type="file"
          id="video"
          name="video"
          onChange={handleVideoChange}
          accept="video/*"
        />

        {videoPreview && (
          <div>
            <label>Video Preview:</label>
            <video
              src={videoPreview}
              controls
              style={{ width: "100%", height: "auto" }}
            />
          </div>
        )}
        <button type="submit">Add Exercise</button>
      </form>
      <Footer />
    </div>
  );
};
