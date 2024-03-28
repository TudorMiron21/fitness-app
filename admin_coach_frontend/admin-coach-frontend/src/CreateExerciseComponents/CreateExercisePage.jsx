import React, { useEffect, useState } from "react";
import "./CreateExercisePage.css";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";
import axios from "axios";
import { Spinner } from "../SpinnerComponents/Spinner";
import { validateToken } from "../utils/auth";
import { useNavigate } from "react-router-dom";

export const CreateExercise = () => {
  const initialState = {
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
  };

  const [exercise, setExercise] = useState(initialState);

  const [loading, setLoading] = useState(false);

  const [imagePreviews, setImagePreviews] = useState({
    before: null,
    after: null,
  });
  const [videoPreview, setVideoPreview] = useState(null);

  const [completionMessage, setCompletionMessage] = useState("");

  const navigate = useNavigate();

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
    setLoading(true);

    try {
      const { uploadId, uploadUrls, exerciseId } = await uploadExerciseDetails(
        exercise.name,
        exercise.description,
        exercise.video ? exercise.video.size : 0,
        exercise.video ? exercise.video.name : "",
        exercise.photos.before,
        exercise.photos.after,
        exercise.equipment,
        exercise.muscleGroup,
        exercise.difficulty,
        exercise.category
      );

      console.log(exerciseId);

      if (uploadId) {
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
          uploadId,
          exerciseId,
          exercise.video.name
        );

        if (completionResponse == true) {
          setCompletionMessage("Exercise added successfully!");
          setExercise(initialState);
        } else {
          setCompletionMessage("Failed to add exercise. Please try again.");
        }
        setVideoPreview(null);
      } else {
        setCompletionMessage("Exercise added successfully!");
        setExercise(initialState);
      }
      setImagePreviews({ before: null, after: null });
    } catch (error) {
      console.error("An error occurred:", error);
      setCompletionMessage("Failed to add exercise. Please try again.");
    } finally {
      setLoading(false);
    }

    revokePreviewUrls();
  }

  async function uploadExerciseDetails(
    exerciseName,
    description,
    videoSize,
    videoName,
    beforeImage,
    afterImage,
    equipmentName,
    muscleGroupName,
    difficultyName,
    categoryName
  ) {
    try {
      const formData = new FormData();
      formData.append("exerciseName", exerciseName);
      formData.append("description", description);
      formData.append("videoSize", videoSize);
      formData.append("videoName", videoName);
      formData.append("beforeImage", beforeImage);
      formData.append("afterImage", afterImage);
      formData.append("equipmentName", equipmentName);
      formData.append("muscleGroupName", muscleGroupName);
      formData.append("difficultyName", difficultyName);
      formData.append("categoryName", categoryName);

      console.log(formData);
      const response = await axios.post(
        "http://localhost:8080/api/v1/adminCoachService/coach/uploadExerciseDetails",

        formData,

        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );

      const { uploadId, uploadUrls, exerciseId } = response.data;

      return { uploadId, uploadUrls, exerciseId };
    } catch (error) {
      console.error("Failed to upload exercise details:", error);
      throw error;
    }
  }

  async function uploadChunkToPresignedUrl(presignedUrl, chunk) {
    let response;

    try {
      response = await fetch(presignedUrl, {
        method: "PUT",
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

  async function completeMultipartUpload(uploadId, exerciseId, filename) {
    // console.log(exerciseId)
    const response = await axios.put(
      "http://localhost:8080/api/v1/adminCoachService/coach/completeMultipartUpload",
      {
        exerciseId,
        uploadId,
        filename,
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
    return response.data;
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
          <option value="">Select Muscle Group</option>
          <option value="Forearms">Forearms</option>
          <option value="Quadriceps">Quadriceps</option>
          <option value="Abdominals">Abdominals</option>
          <option value="Lats">Lats</option>
          <option value="Middle Back">Middle Back</option>
          <option value="Lower Back">Lower Back</option>
          <option value="Shoulders">Shoulders</option>
          <option value="Biceps">Biceps</option>
          <option value="Glutes">Glutes</option>
          <option value="Triceps">Triceps</option>
          <option value="Hamstrings">Hamstrings</option>
          <option value="Neck">Neck</option>
          <option value="Chest">Chest</option>
          <option value="Traps">Traps</option>
          <option value="Calves">Calves</option>
          <option value="Adductors">Adductors</option>
          <option value="Abductors">Abductors</option>
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
          <option value="">Select Category</option>
          <option value="Strongman">Strongman</option>
          <option value="Strength">Strength</option>
          <option value="Olympic Weightlifting">Olympic Weightlifting</option>
          <option value="Powerlifting">Powerlifting</option>
          <option value="Cardio">Cardio</option>
          <option value="Plyometrics">Plyometrics</option>
          <option value="Stretching">Stretching</option>
        </select>

        <label htmlFor="difficulty">Difficulty Level:</label>
        <select
          id="difficulty"
          name="difficulty"
          value={exercise.difficulty}
          onChange={handleInputChange}
          required
        >
          <option value="">Select Difficulty</option>
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
          <option value="">Select Equipment</option>
          <option value="Other">Other</option>
          <option value="Machine">Machine</option>
          <option value="Barbell">Barbell</option>
          <option value="Dumbbell">Dumbbell</option>
          <option value="Body Only">Body Only</option>
          <option value="Kettlebells">Kettlebells</option>
          <option value="Cable">Cable</option>
          <option value="E-Z Curl Bar">E-Z Curl Bar</option>
          <option value="Bands">Bands</option>
          <option value="Medicine Ball">Medicine Ball</option>
          <option value="Exercise Ball">Exercise Ball</option>
        </select>

        <label htmlFor="photos-before">Before Photo:</label>
        <input
          type="file"
          id="photos-before"
          name="before"
          onChange={handlePhotoChange}
          accept="image/*"
          required
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
          required
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
      {loading && <Spinner />}

      {completionMessage && (
        <div className="completion-message">{completionMessage}</div>
      )}

      <Footer />
    </div>
  );
};
