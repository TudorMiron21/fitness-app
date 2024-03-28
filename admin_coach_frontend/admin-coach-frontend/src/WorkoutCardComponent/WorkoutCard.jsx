import React from 'react';
import './WorkoutCard.css'; // Importing the CSS file for styling

export const WorkoutCard = ({ dayIndex, workout }) => {
  return (
    <div className="workout-box">
      <div className="workout-day">Day {dayIndex}</div>
      {workout ? (
        <div className="workout-details">
          {workout.coverPhotoUrl && (
            <img
              src={workout.coverPhotoUrl}
              alt={`Cover for ${workout.name}`}
              className="workout-cover"
            />
          )}
          <h3 className="workout-name">{workout.name}</h3>
          <p className="workout-difficulty">Difficulty: {workout.difficultyLevel}</p>
          {/* Additional workout details can be added here */}
        </div>
      ) : (
        <div className="rest-day">Rest Day</div>
      )}
    </div>
  );
};