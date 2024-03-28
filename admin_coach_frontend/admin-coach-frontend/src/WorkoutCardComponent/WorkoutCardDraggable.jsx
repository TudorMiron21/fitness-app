import React from 'react';
import './WorkoutCardDraggable.css'; // Importing the CSS file for styling

export const WorkoutCardDraggable = ({workout, onDragStart, onDragOver, onDrop }) => {
  // Function to handle the start of dragging
  const handleDragStart = (event, workout) => {
    event.dataTransfer.setData('application/reactflow', workout.id);
    event.dataTransfer.effectAllowed = 'move';
  };

  const handleDragEnd = (e) => {
    const target = e.target;
    target.style.display = "block";
  };

  return (
    <div
      className="workout-draggable-box"
      draggable
      onDragStart={(event) => handleDragStart(event, workout)}
      onDragEnd={handleDragEnd}
      onDragOver={onDragOver}
      
    >
      <div className="draggable-workout-details">
        {workout.coverPhotoUrl && (
          <img
            src={workout.coverPhotoUrl}
            alt={`Cover for ${workout.name}`}
            className="draggable-workout-cover"
          />
        )}
        <h3 className="draggable-workout-name">{workout.name}</h3>
        <p className="draggable-workout-difficulty">Difficulty: {workout.difficultyLevel}</p>
      </div>
    </div>
  );
};