import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";
import { validateToken } from "../utils/auth";
import { WorkoutCard } from "../WorkoutCardComponent/WorkoutCard";

import "./CreateProgramPage.css";
export const CreateProgramPage = () => {
  const initialProgramState = {
    programName: "",
    description: "",
    durationInDays: 0,
    coverPhono: null,
  };

  const navigate = useNavigate();

  const [workouts, setWorkouts] = useState([]);

  const [programForm, setProgramForm] = useState(initialProgramState);

  const [isSetDurationPressed, setIsSetDurationPressed] = useState(false);

  const [isTokenValid, setIsTokenValid] = useState(false);

  const [isWorkoutContainerVisible, setIsWorkoutContainerVisible] =
    useState(false);

  const [workoutMap, setWorkoutMap] = useState(new Map());

  const [imagePreview, setImagePreview] = useState(null);

  useEffect(() => {
    const isTokenValid = async () => {
      try {
        const token = localStorage.getItem("access_token");

        if (token) {
          setIsTokenValid(await validateToken(token));
        }
      } catch (error) {
        console.error("Error fetching coach details status", error);
      }
    };

    isTokenValid();
  }, []);

  const handleSetDuration = () => {
    const newDuration = programForm.durationInDays;

    const newWorkoutMap = new Map();
    for (let i = 1; i <= newDuration; i++) {
      newWorkoutMap.set(i, null);
    }

    setWorkoutMap(newWorkoutMap);
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
  const handleGetWorkouts = async () => {};
  const handleProgramSubmit = async (e) => {
    e.preventDefault();
  };

  if (!isTokenValid) {
    navigate("/login");
  } else {
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
                  <WorkoutCard
                    key={dayIndex}
                    dayIndex={dayIndex}
                    workout={workout}
                  />
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

          <div className="workouts-container"></div>
        </div>
        <Footer />
      </div>
    );
  }
};
