import "./CreateWorkoutPage.css"; // Ensure this path is correct
import { validateToken } from "../utils/auth";
import React, { useEffect, useState } from "react";
import { useNavigate } from "react-router-dom";
import { NavBar } from "../NavBarComponents/NavBar";
import { Footer } from "../FooterComponent/Footer";
import axios from "axios";
import { ExerciseCard } from "../ExerciseCardComponent/ExerciseCard";
import { Spinner } from "../SpinnerComponents/Spinner";

export const CreateWorkoutPage = () => {
  const initilaWorkoutState = {
    workoutName: "",
    description: "",
    coverPhoto: null,
  };

  const [workoutForm, setWorkoutForm] = useState(initilaWorkoutState);

  const [filters, setFilters] = useState({
    name: "",
    isExercisePrivate: false,
    muscleGroupNames: [],
    equipmentNames: [],
    difficultyNames: [],
    categoryNames: [],
  });

  const [exercises, setExercises] = useState([]);

  const [addedExercises, setAddedExercises] = useState([]);

  const [completionMessage, setCompletionMessage] = useState("");

  const [loading, setLoading] = useState(false);

  const addOrRemoveExercise = (selectedExercise) => {
    setAddedExercises((prevExercises) => {
      const exerciseExists = prevExercises.some(
        (e) => e.exerciseId === selectedExercise.exerciseId
      );

      if (exerciseExists) {
        return prevExercises.filter(
          (e) => e.exerciseId !== selectedExercise.exerciseId
        );
      } else {
        return [...prevExercises, selectedExercise];
      }
    });
  };
  const [imagePreview, setImagePreview] = useState(null);

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

  const [isFilterVisible, setIsFilterVisible] = useState(false);
  const toggleFilter = () => {
    setIsFilterVisible(!isFilterVisible);
  };

  const [isExerciseContainerVisible, setIsExerciseContainerVisible] =
    useState(false);
  const handleAddExercisesClick = async () => {
    setIsExerciseContainerVisible(!isExerciseContainerVisible);

    await getFilteredExercises();
  };

  const handleApplyFilters = async () => {
    await getFilteredExercises();
  };

  const getFilteredExercises = async () => {
    const queryParams = new URLSearchParams();

    if (filters.name) queryParams.append("name", filters.name);
    queryParams.append("isExercisePrivate", filters.isExercisePrivate);
    if (filters.muscleGroupNames)
      filters.muscleGroupNames.forEach((mg) =>
        queryParams.append("muscleGroupNames", mg)
      );
    if (filters.equipmentNames)
      filters.equipmentNames.forEach((eq) =>
        queryParams.append("equipmentNames", eq)
      );
    if (filters.difficultyNames)
      filters.difficultyNames.forEach((d) =>
        queryParams.append("difficultyNames", d)
      );
    if (filters.categoryNames)
      filters.categoryNames.forEach((c) =>
        queryParams.append("categoryNames", c)
      );

    try {
      console.log(
        `http://localhost:8080/api/v1/adminCoachService/coach/getFilteredExercises?${queryParams.toString()}`
      );
      const response = await axios.get(
        `http://localhost:8080/api/v1/adminCoachService/coach/getFilteredExercises?${queryParams.toString()}`,
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      console.log(response.data);
      setExercises(response.data);
    } catch (err) {
      console.error("Error loading filtered exercises", err);
    }
  };

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

  const handlePrivateCheckboxChange = (event) => {
    setFilters({
      ...filters,
      isExercisePrivate: event.target.checked,
    });
  };

  const handleSubmitWorkout = async (e) => {
    e.preventDefault();
    setLoading(true);

    const formDataToSend = new FormData();

    formDataToSend.append("name", workoutForm.workoutName);
    formDataToSend.append("description", workoutForm.description);
    formDataToSend.append(
      "exerciseIds",
      addedExercises.map((exercise) => exercise.exerciseId).join(",")
    );
    formDataToSend.append("coverPhoto", workoutForm.coverPhoto);

    console.log(
      addedExercises.map((exercise) => exercise.exerciseId).join(",")
    );
    try {
      const response = await axios.post(
        "http://localhost:8080/api/v1/adminCoachService/coach/createWorkout",
        formDataToSend,
        {
          headers: {
            "Content-Type": "multipart/form-data",
            Authorization: `Bearer ${localStorage.getItem("access_token")}`,
          },
        }
      );
      if (response.status == 200) {
        setCompletionMessage("Workout added successfully!");
        setWorkoutForm(initilaWorkoutState);
        setAddedExercises([]);
        setImagePreview([]);
        setIsExerciseContainerVisible(false);
        e.target.reset();
      } else {
        setCompletionMessage("Failed to add workout. Please try again.");
      }
    } catch (err) {
    } finally {
      setLoading(false);
    }
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

  const handleCategoryFilterChange = (event) => {
    const { value } = event.target;
    const newCategories = [...filters.categoryNames];
    if (newCategories.includes(value)) {
      setFilters({
        ...filters,
        categoryNames: newCategories.filter((category) => category !== value),
      });
    } else {
      newCategories.push(value);
      setFilters({
        ...filters,
        categoryNames: newCategories,
      });
    }
  };

  const handleDifficultyFilterChange = (event) => {
    const { value } = event.target;
    const newDifficulties = [...filters.difficultyNames];
    if (newDifficulties.includes(value)) {
      setFilters({
        ...filters,
        difficultyNames: newDifficulties.filter(
          (difficulty) => difficulty !== value
        ),
      });
    } else {
      newDifficulties.push(value);
      setFilters({
        ...filters,
        difficultyNames: newDifficulties,
      });
    }
  };

  const handleEquipmentFilterChange = (event) => {
    const { value } = event.target;
    const newEquipments = [...filters.equipmentNames];
    if (newEquipments.includes(value)) {
      setFilters({
        ...filters,
        equipmentNames: newEquipments.filter(
          (equipment) => equipment !== value
        ),
      });
    } else {
      newEquipments.push(value);
      setFilters({
        ...filters,
        equipmentNames: newEquipments,
      });
    }
  };

  return (
    <div>
      <NavBar />
      <div
        className={`create-workout-page ${
          isExerciseContainerVisible ? "with-filters" : ""
        }`}
      >
        {" "}
        <div className="workout-form-container">
          <h2>Create Workout</h2>
          <form onSubmit={handleSubmitWorkout}>
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
              <button
                type="button"
                className="button"
                onClick={handleAddExercisesClick}
              >
                Add Exercises
              </button>
            </div>

            <div className="exercise-list">
              {addedExercises.map((exercise) => (
                <div
                  key={exercise.exerciseId}
                  // onClick={() => addOrRemoveExercise(exercise)}
                >
                  <ExerciseCard exercise={exercise} focusEnabled={false} />
                </div>
              ))}
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
              <button className="button">Submit Workout</button>
            </div>
          </form>
          {loading && <Spinner />}

          {completionMessage && (
            <div className="completion-message">{completionMessage}</div>
          )}
        </div>
        <div
          className={`exercise-filters-container ${
            isExerciseContainerVisible ? "visible" : ""
          }`}
        >
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
                    ].map((category) => (
                      <label key={category}>
                        <input
                          type="checkbox"
                          name="category"
                          value={category}
                          checked={filters.categoryNames.includes(category)}
                          onChange={handleCategoryFilterChange}
                        />
                        {category}
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
                      (difficulty) => (
                        <label key={difficulty}>
                          <input
                            type="checkbox"
                            name="difficulty"
                            value={difficulty}
                            checked={filters.difficultyNames.includes(
                              difficulty
                            )}
                            onChange={handleDifficultyFilterChange}
                          />
                          {difficulty}
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
                    ].map((equipment) => (
                      <label key={equipment}>
                        <input
                          type="checkbox"
                          name="equipment"
                          value={equipment}
                          checked={filters.equipmentNames.includes(equipment)}
                          onChange={handleEquipmentFilterChange}
                        />
                        {equipment}
                      </label>
                    ))}
                  </div>
                )}
              </div>

              <div className="filter-item">
                <label htmlFor="isPrivate">Only Exercises Created By You</label>
                <input
                  type="checkbox"
                  id="isPrivate"
                  name="isPrivate"
                  checked={filters.isExercisePrivate}
                  onChange={handlePrivateCheckboxChange}
                />
              </div>

              <div>
                <button
                  onClick={handleApplyFilters}
                  className="apply-filters-button"
                >
                  Apply FIlters
                </button>
              </div>
            </div>
          )}
          <div>
            <button onClick={toggleFilter} className="button">
              {isFilterVisible ? "Hide Filters" : "Show Filters"}
            </button>
          </div>

          <div className="exercise-list">
            {exercises.map((exercise) => (
              <div
                key={exercise.exerciseId}
                onClick={() => addOrRemoveExercise(exercise)}
              >
                <ExerciseCard exercise={exercise} focusEnabled={true} />
              </div>
            ))}
          </div>
          {/* Rest of the component */}
        </div>
      </div>
      <Footer />
    </div>
  );
};
