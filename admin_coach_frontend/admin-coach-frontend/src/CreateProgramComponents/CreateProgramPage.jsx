import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";
import { validateToken } from "../utils/auth";
import { WorkoutCard } from "../WorkoutCardComponent/WorkoutCard";
import { WorkoutCardDraggable } from "../WorkoutCardComponent/WorkoutCardDraggable";
import "./CreateProgramPage.css";
import axios from "axios";

export const CreateProgramPage = () => {
  const initialProgramState = {
    programName: "",
    description: "",
    durationInDays: 0,
    coverPhoto: null,
  };

  const [filters, setFilters] = useState({
    name: "",
    isWorkoutPrivate: false,
    maxDifficultyLevel: 3.0,
    minDifficultyLevel: 1.0,
  });

  const navigate = useNavigate();

  const [workouts, setWorkouts] = useState([]);

  const [programForm, setProgramForm] = useState(initialProgramState);

  const [isSetDurationPressed, setIsSetDurationPressed] = useState(false);

  const [isWorkoutContainerVisible, setIsWorkoutContainerVisible] =
    useState(false);

  const [workoutMap, setWorkoutMap] = useState(new Map());

  const [imagePreview, setImagePreview] = useState(null);

  const [loading, setLoading] = useState(false);

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

  const handleInputChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: value,
    }));
  };

  const handleCheckboxChange = (e) => {
    const { name, checked } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: checked,
    }));
  };

  const handleRangeChange = (e) => {
    const { name, value } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: parseFloat(value),
    }));
  };

  const handleDragOver = (event) => {
    event.preventDefault();
    event.dataTransfer.dropEffect = "move";
  };

  const handleDrop = (event, dayIndex) => {
    event.preventDefault();
    const workoutId = event.dataTransfer.getData("application/reactflow");
    // Find the dragged workout using the id
    const workoutToAssign = workouts.find(
      (workout) => workout.id === parseInt(workoutId)
    );
    setWorkoutMap((prevMap) => {
      const newMap = new Map(prevMap);
      newMap.set(dayIndex, workoutToAssign);
      return newMap;
    });
  };

  const getFilteredWorkouts = async () => {
    const queryParams = new URLSearchParams();

    if (filters.name) queryParams.append("name", filters.name);
    queryParams.append("isWorkoutPrivate", filters.isWorkoutPrivate);
    queryParams.append("maxDifficultyLevel", filters.maxDifficultyLevel);
    queryParams.append("minDifficultyLevel", filters.minDifficultyLevel);

    const response = await axios.get(
      `http://localhost:8080/api/v1/adminCoachService/coach/getFilteredWorkouts?${queryParams.toString()}`,

      {
        headers: {
          Authorization: `Bearer ${localStorage.getItem("access_token")}`,
        },
      }
    );

    return response.data;
  };

  const handleApplyFilter = async () => {
    setWorkouts(await getFilteredWorkouts());
  };

  const handleSetDuration = async () => {
    const newDuration = programForm.durationInDays;

    const newWorkoutMap = new Map();
    for (let i = 1; i <= newDuration; i++) {
      newWorkoutMap.set(i, null);
    }

    setWorkoutMap(newWorkoutMap);

    setWorkouts(await getFilteredWorkouts());

    setIsWorkoutContainerVisible(true);
  };

  const handleFormInputChange = (e) => {
    const { name, value } = e.target;
    setProgramForm((prevForm) => ({
      ...prevForm,
      [name]: value,
    }));
  };

  const handlePhotoChange = (event) => {
    const { files } = event.target;
    const file = files[0];
    if (file) {
      setProgramForm((prevForm) => ({
        ...prevForm,
        coverPhoto: file,
      }));

      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleProgramSubmit = async (e) => {
    e.preventDefault();
    setLoading(true);

    const indexedWorkoutsObject = Object.fromEntries(
      Array.from(workoutMap, ([key, value]) => [
        String(key),
        value != null ? value.id : null,
      ])
    );
    console.log(indexedWorkoutsObject);
    const createProgramForm = {
      name: programForm.programName,
      description: programForm.description,
      durationInDays: programForm.durationInDays,
      indexedWorkouts: indexedWorkoutsObject,
    };

    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/adminCoachService/coach/createProgram",
        createProgramForm,
        {
          headers: {
            "Content-Type": "application/json",
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );

      if (response.status === 200) {
        const programId = response.data.id;

        try {
          const formData = new FormData();
          formData.append("coverPhoto", programForm.coverPhoto);
          const responseCoverPhotoUpload = await axios.put(
            `http://localhost:8080/api/v1/adminCoachService/coach/uploadProgramCoverPhoto/${programId}`,
            formData,
            {
              headers: {
                "Content-Type": "multipart/form-data",
                Authorization: `Bearer ${localStorage.getItem("access_token")}`,
              },
            }
          );

          if (responseCoverPhotoUpload.status === 200) {
            console.log("Cover photo uploaded successfully");
          } else {
            console.error(
              "Cover photo upload failed with status:",
              responseCoverPhotoUpload.status
            );
          }
        } catch (error) {
          console.error("Error uploading cover photo:", error);
        }
      } else {
        console.error("Program creation failed with status:", response.status);
      }
    } catch (error) {
      console.error("There was an error creating the program:", error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <div>
      <NavBar />
      <div
        className={`create-program-page ${
          isWorkoutContainerVisible ? "workouts-visible" : ""
        }`}
      >
        <div className="program-form-container">
          <h2>Create Program</h2>

          <form onSubmit={handleProgramSubmit}>
            <div className="program-input-group">
              <label htmlFor="programName">Program Name:</label>
              <input
                type="text"
                id="programName"
                name="programName"
                value={programForm.programName}
                onChange={handleFormInputChange}
                placeholder="Program Name"
              />
            </div>

            <div className="program-input-group">
              <label htmlFor="description">Description:</label>
              <textarea
                id="description"
                name="description"
                value={programForm.description}
                onChange={handleFormInputChange}
                placeholder="Program Description"
              ></textarea>
            </div>

            <div className="program-input-group">
              <label htmlFor="duration">Duration In Days :</label>
              <input
                type="number"
                id="duration"
                name="durationInDays"
                value={programForm.durationInDays}
                onChange={handleFormInputChange}
                min="1"
                step="1"
              ></input>
              <button
                type="button"
                onClick={handleSetDuration}
                className="button"
              >
                Set Duration
              </button>
            </div>

            <div className="workout-cards-container">
              {Array.from(workoutMap.entries()).map(([dayIndex, workout]) => (
                <div
                  key={dayIndex}
                  onDragOver={handleDragOver}
                  onDrop={(event) => handleDrop(event, dayIndex)}
                  className="drop-zone"
                >
                  <WorkoutCard
                    key={dayIndex}
                    dayIndex={dayIndex}
                    workout={workout}
                  />
                </div>
              ))}
            </div>

            <div className="program-input-group">
              <label htmlFor="cover-photo">Cover Photo:</label>
              <input
                type="file"
                id="cover-photo"
                name="coverPhoto"
                onChange={handlePhotoChange}
                accept="image/*"
              />
            </div>
            {imagePreview && (
              <div className="program-image-preview-container">
                <label>Cover Photo Preview:</label>
                <img src={imagePreview} alt="Cover Preview" />
              </div>
            )}

            <div className="program-input-group">
              <button className="button">Submit Workout</button>
            </div>
          </form>
        </div>

        <div
          className={`workouts-container :${
            isWorkoutContainerVisible ? "visible" : ""
          }`}
        >
          <div className="filters">
            <input
              type="text"
              name="name"
              placeholder="Search by name"
              value={filters.name}
              onChange={handleInputChange}
            />

            <label className="block">
              <input
                type="checkbox"
                name="isWorkoutPrivate"
                checked={filters.isWorkoutPrivate}
                onChange={handleCheckboxChange}
              />
              Private Workout
            </label>

            <label className="block">
              Min Difficulty Level
              <div className="difficulty-level">
                <span>1.0</span>
                <span>3.0</span>
              </div>
              <input
                type="range"
                name="minDifficultyLevel"
                min="1.0"
                max="3.0"
                step="0.1"
                value={filters.minDifficultyLevel}
                onChange={handleRangeChange}
              />
              <div className="difficulty-level">
                <span>{filters.minDifficultyLevel}</span>
              </div>
            </label>

            <label className="block">
              Max Difficulty Level
              <div className="difficulty-level">
                <span>1.0</span>
                <span>3.0</span>
              </div>
              <input
                type="range"
                name="maxDifficultyLevel"
                min="1.0"
                max="3.0"
                step="0.1"
                value={filters.maxDifficultyLevel}
                onChange={handleRangeChange}
              />
              <div className="difficulty-level">
                <span>{filters.maxDifficultyLevel}</span>
              </div>
              <button
                type="button"
                className="button"
                onClick={handleApplyFilter}
              >
                Apply Filters
              </button>
            </label>
          </div>

          <div className="workout-draggable-container">
            {workouts.map((workout) => (
              <WorkoutCardDraggable
                workout={workout}
                onDragOver={handleDragOver}
              />
            ))}
          </div>
        </div>
      </div>
      <Footer />
    </div>
  );
};
