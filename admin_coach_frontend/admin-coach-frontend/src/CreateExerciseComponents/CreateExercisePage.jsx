import React, { useState } from "react";
import "./CreateExercisePage.css";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";
import axios from 'axios';

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

  async function handleSubmit(event) {
    event.preventDefault();

    console.log(exercise.video.size);
    const { uploadId, uploadUrls } = await uploadExerciseDetails(
      exercise.video.size
    );

    console.log(uploadUrls);
    const fileSize = exercise.video.size;
    const chunkSize = Math.ceil(fileSize / uploadUrls.length); // size of each chunk
    const chunks = [];

    for (let start = 0; start < fileSize; start += chunkSize) {
      const end = Math.min(start + chunkSize, fileSize);
      chunks.push(exercise.video.slice(start, end));
    }
    const uploadPromises = chunks.map((chunk, index) =>
      uploadChunkToPresignedUrl(uploadUrls[index], chunk)
    );

    const uploadResults = await Promise.all(uploadPromises);

    const completionResponse = await completeMultipartUpload(
      uploadId
    );

    revokePreviewUrls();
  }

  async function uploadExerciseDetails(videoSize) {
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/adminCoachService/coach/uploadExerciseDetails",
        { videoSize },
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
  
      const { uploadId, uploadUrls } = response.data;
  
      return { uploadId, uploadUrls };
    } catch (error) {
      console.error("Failed to upload exercise details:", error);
      throw error;
    }
  }

  async function uploadChunkToPresignedUrl(presignedUrl, chunk) {
    let response;
  
    try {
      response = await fetch(presignedUrl, {
        method: 'PUT',
        body: chunk, // assuming chunk is already a Blob or similar object
        headers: {
          "Content-Type": "application/octet-stream",
        },
      });
    } catch (error) {
      // Network errors or CORS issues will be caught here.
      console.error("Error while uploading chunk:", error);
      throw new Error("Chunk upload failed due to network error");
    }
  
    if (!response.ok) {
      // Server responded with HTTP error code.
      console.error("Response error while uploading chunk:", response);
      throw new Error(`Chunk upload failed with status: ${response.status}`);
    }
  
    return response.headers.get("ETag");
  }

  async function completeMultipartUpload(uploadId) {
    const response = await axios.put(
      "http://localhost:8080/api/v1/adminCoachService/coach/completeMultipartUpload",
      {
        uploadId,
      },
      {
        headers: {
          "Content-Type": "application/json",
          Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        },
      }
    );

    if (response.data == true) {
      console.log("upload success " + response.status);
    } else {
      console.log("upload failed" + response.status);
    }
  }

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
