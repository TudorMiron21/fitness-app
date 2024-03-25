import React, { useState } from "react";
import "./ExerciseCard.css";

export const ExerciseCard = ({ exercise, focusEnabled }) => {
  const [isSelected, setIsSelected] = useState(false); // State to track if the card is selected

  if (!exercise) return null; // Return null if no exercise data is provided

  const toggleSelect = () => {
    if (focusEnabled) setIsSelected(!isSelected); // Toggle the selected state
  };

  return (
    <div
      className={`exercise-card ${isSelected ? "selected" : ""}`}
      onClick={toggleSelect}
    >
      <div className="exercise-image-container">
        {exercise.exerciseImageStartUrl && (
          <img
            src={exercise.exerciseImageStartUrl}
            alt={`Start position for ${exercise.name}`}
            className="exercise-image"
          />
        )}
      </div>
      <div className="exercise-details">
        <h2 className="exercise-name">{exercise.name}</h2>
        <p className="exercise-description">{exercise.description}</p>
        <ul className="exercise-info">
          <li>
            <strong>Muscle Group:</strong> {exercise.muscleGroup?.name}
          </li>
          <li>
            <strong>Equipment:</strong> {exercise.equipment?.name}
          </li>
          <li>
            <strong>Difficulty:</strong> {exercise.difficulty?.dificultyLevel}
          </li>
          <li>
            <strong>Category:</strong> {exercise.category?.name}
          </li>
          <li>
            <strong>Exclusive:</strong>{" "}
            {exercise.isExerciseExclusive ? "Yes" : "No"}
          </li>
        </ul>
      </div>
    </div>
  );
};
