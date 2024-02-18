package tudor.work.model;


import lombok.*;
import tudor.work.embeddables.ResultExercisePersonalRecordId;

import javax.persistence.*;

@Entity
@Table
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultExercisePersonalRecord {

    @EmbeddedId
    private ResultExercisePersonalRecordId id;

    @ManyToOne
    @MapsId("idWorkoutResult")
    @JoinColumn(name = "id_workout_result")
    private WorkoutResult workoutResult;

    @ManyToOne
    @MapsId("idExercise")
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    private Double maxWeight;

    private Double maxCalories;

    private Integer maxReps;

}
