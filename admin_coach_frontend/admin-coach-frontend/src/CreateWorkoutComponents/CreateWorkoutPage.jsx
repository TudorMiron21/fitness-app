import "./CreateWorkoutPage.css"; // Ensure this path is correct
import { validateToken } from "../utils/auth";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";

const CreateWorkoutPage = () => {
  const [workoutForm, setWorkoutForm] = useState({
    workoutName: "",
    description: "",
    coverPhoto: null,
  });

  const [filters, setFilters] = useState({
    name: "",
    isExercisePrivate: false,
    muscleGroupNames: [],
    equipmentNames: [],
    difficultyNames: [],
    categoryNames: [],
  });

  const [imagePreview, setImagePreview] = useState(null);
  const [isTokenValid, setIsTokenValid] = useState(false);

  const navigate = useNavigate();
  const [isMuscleGroupVisible, setIsMuscleGroupVisible] = useState(false);

  const toggleMuscleGroupVisibility = () => {
    setIsMuscleGroupVisible(!isMuscleGroupVisible);
  };

  const [isCategoryVisible, setIsCategoryVisible] = useState(false);
  const toggleCategoryVisibility = () => {
    setIsCategoryVisible(!isCategoryVisible);
  };

  const [isDifficultyVisible, setIsDifficultyVisible] = useState(false);
  const toggleDifficultyVisibility = () => {
    setIsDifficultyVisible(!isDifficultyVisible);
  };

  const [isEquipmentVisible, setIsEquipmentVisible] = useState(false);
  const toggleEquipmentVisibility = () => {
    setIsEquipmentVisible(!isEquipmentVisible);
  };

  const [isExercisePrivate, setIsExercisePrivate] = useState(false);
  const handlePrivateCheckboxChange = (event) => {
    setIsExercisePrivate(event.target.checked);
  };

  const [isFilterVisible, setIsFilterVisible] = useState(false);
  const toggleFilter = () => {
    setIsFilterVisible(!isFilterVisible);
  };

  const [isExerciseContainerVisible, setIsExerciseContainerVisible] =
    useState(false);
  const handleAddExercisesClick = () => {
    setIsExerciseContainerVisible(!isExerciseContainerVisible);
  };

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
  });
  const handleFormInputChange = (e) => {
    const { name, value } = e.target;
    setWorkoutForm((prevForm) => ({
      ...prevForm,
      [name]: value,
    }));
  };

  const handlePhotoChange = (event) => {
    const { files } = event.target;
    const file = files[0];
    if (file) {
      setWorkoutForm((prevForm) => ({
        ...prevForm,
        coverPhoto: file,
      }));

      setImagePreview(URL.createObjectURL(file));
    }
  };

  const handleFilterInputChange = (e) => {
    const { name, value, type, checked } = e.target;
    setFilters((prevFilters) => ({
      ...prevFilters,
      [name]: type === "checkbox" ? checked : value,
    }));
  };

  const handleSubmit = (e) => {
    e.preventDefault();
    // Submit form logic here
    console.log(workoutForm);
    // Assuming you have a function to handle the submission
    // submitWorkoutForm(workoutForm);
  };

  const handleMuscleGroupFilterChange = (event) => {
    const { value } = event.target;
    const newMuscleGroups = [...filters.muscleGroupNames];
    if (newMuscleGroups.includes(value)) {
      // Remove the muscle group if it's already in the array
      setFilters({
        ...filters,
        muscleGroupNames: newMuscleGroups.filter(
          (muscleGroup) => muscleGroup !== value
        ),
      });
    } else {
      // Add the muscle group to the array
      newMuscleGroups.push(value);
      setFilters({
        ...filters,
        muscleGroupNames: newMuscleGroups,
      });
    }
  };
  if (!isTokenValid) {
    navigate("/login");
  } else {
    return (
      <div>
        <NavBar />
        <div className="create-workout-page">
          <div className="workout-form-container">
            <h2>Create Workout</h2>
            <form onSubmit={handleSubmit}>
              <div className="input-group">
                <label htmlFor="workoutName">Workout Name:</label>
                <input
                  type="text"
                  id="workoutName"
                  name="workoutName"
                  value={workoutForm.workoutName}
                  onChange={handleFormInputChange}
                  placeholder="Workout Name"
                />
              </div>
              <div className="input-group">
                <label htmlFor="description">Description:</label>
                <textarea
                  id="description"
                  name="description"
                  value={workoutForm.description}
                  onChange={handleFormInputChange}
                  placeholder="Workout Description"
                ></textarea>
              </div>

              <div className="input-group">
                <button className="button" onClick={handleAddExercisesClick}>
                  Add Exercises
                </button>
              </div>

              <div className="input-group">
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
                <div className="image-preview-container">
                  <label>Cover Photo Preview:</label>
                  <img src={imagePreview} alt="Cover Preview" />
                </div>
              )}
              <div className="input-group">
                <button className="button">Submit Workout Plan</button>
              </div>
            </form>
          </div>

          <div
            className={`exercise-filters-container ${
              isExerciseContainerVisible ? "" : "hidden"
            }`}
          >
            {" "}
            {isFilterVisible && (
              <div
                className={`filters-content ${isFilterVisible ? "" : "hidden"}`}
              >
                {/* Exercise Name Input */}
                <div className="input-group">
                  <input
                    type="text"
                    name="name"
                    value={filters.name}
                    onChange={handleFilterInputChange}
                    placeholder="Exercise Name"
                    className="exercise-name-input"
                  />
                </div>

                {/* Dropdown Filters */}
                <div className="filter-item">
                  <div
                    className="dropdown-label"
                    onClick={toggleMuscleGroupVisibility}
                  >
                    <label>Muscle Group</label>
                    <span className="dropdown-toggle">
                      {isMuscleGroupVisible ? "▲" : "▼"}
                    </span>
                  </div>
                  {isMuscleGroupVisible && (
                    <div className="checkboxes">
                      {[
                        "Forearms",
                        "Quadriceps",
                        "Abdominals",
                        "Lats",
                        "Middle Back",
                        "Lower Back",
                        "Shoulders",
                        "Biceps",
                        "Glutes",
                        "Triceps",
                        "Hamstrings",
                        "Neck",
                        "Chest",
                        "Traps",
                        "Calves",
                        "Adductors",
                        "Abductors",
                      ].map((muscleGroup) => (
                        <label key={muscleGroup}>
                          <input
                            type="checkbox"
                            name="muscleGroup"
                            value={muscleGroup}
                            checked={filters.muscleGroupNames.includes(
                              muscleGroup
                            )}
                            onChange={handleMuscleGroupFilterChange}
                          />
                          {muscleGroup}
                        </label>
                      ))}
                    </div>
                  )}
                </div>

                <div className="filter-item">
                  <div
                    className="dropdown-label"
                    onClick={toggleCategoryVisibility}
                  >
                    <label>Category</label>
                    <span className="dropdown-toggle">
                      {isCategoryVisible ? "▲" : "▼"}
                    </span>
                  </div>
                  {isCategoryVisible && (
                    <div className="checkboxes">
                      {[
                        "Strongman",
                        "Strength",
                        "Olympic Weightlifting",
                        "Powerlifting",
                        "Cardio",
                        "Plyometrics",
                        "Stretching",
                      ].map((muscleGroup) => (
                        <label key={muscleGroup}>
                          <input
                            type="checkbox"
                            name="muscleGroup"
                            value={muscleGroup}
                            checked={filters.muscleGroupNames.includes(
                              muscleGroup
                            )}
                            onChange={handleMuscleGroupFilterChange}
                          />
                          {muscleGroup}
                        </label>
                      ))}
                    </div>
                  )}
                </div>

                <div className="filter-item">
                  <div
                    className="dropdown-label"
                    onClick={toggleDifficultyVisibility}
                  >
                    <label>Difficulty</label>
                    <span className="dropdown-toggle">
                      {isDifficultyVisible ? "▲" : "▼"}
                    </span>
                  </div>
                  {isDifficultyVisible && (
                    <div className="checkboxes">
                      {["Beginner", "Intermediate", "Advanced"].map(
                        (muscleGroup) => (
                          <label key={muscleGroup}>
                            <input
                              type="checkbox"
                              name="muscleGroup"
                              value={muscleGroup}
                              checked={filters.muscleGroupNames.includes(
                                muscleGroup
                              )}
                              onChange={handleMuscleGroupFilterChange}
                            />
                            {muscleGroup}
                          </label>
                        )
                      )}
                    </div>
                  )}
                </div>

                <div className="filter-item">
                  <div
                    className="dropdown-label"
                    onClick={toggleEquipmentVisibility}
                  >
                    <label>Equipment</label>
                    <span className="dropdown-toggle">
                      {isEquipmentVisible ? "▲" : "▼"}
                    </span>
                  </div>
                  {isEquipmentVisible && (
                    <div className="checkboxes">
                      {[
                        "Other",
                        "Machine",
                        "Barbell",
                        "Dumbbell",
                        "Body Only",
                        "Kettlebells",
                        "Cable",
                        "E-Z Curl Bar",
                        "Bands",
                        "Medicine Ball",
                        "Exercise Ball",
                      ].map((muscleGroup) => (
                        <label key={muscleGroup}>
                          <input
                            type="checkbox"
                            name="muscleGroup"
                            value={muscleGroup}
                            checked={filters.muscleGroupNames.includes(
                              muscleGroup
                            )}
                            onChange={handleMuscleGroupFilterChange}
                          />
                          {muscleGroup}
                        </label>
                      ))}
                    </div>
                  )}
                </div>

                <div className="filter-item">
                  <label htmlFor="isPrivate">
                    Only Exercises Created By You
                  </label>
                  <input
                    type="checkbox"
                    id="isPrivate"
                    name="isPrivate"
                    checked={isExercisePrivate}
                    onChange={handlePrivateCheckboxChange}
                  />
                </div>
              </div>
            )}
            <div>
              <button onClick={toggleFilter} className="button">
                {isFilterVisible ? "Hide Filters" : "Show Filters"}
              </button>
            </div>
            {/* Rest of the component */}
          </div>
        </div>
        <Footer />
      </div>
    );
  }
};

export default CreateWorkoutPage;
