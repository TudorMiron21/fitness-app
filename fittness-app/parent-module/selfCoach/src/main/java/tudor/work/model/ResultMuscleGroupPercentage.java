package tudor.work.model;

import lombok.*;
import tudor.work.embeddables.ResultMuscleGroupPercentageId;

import javax.persistence.*;

@Table
@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ResultMuscleGroupPercentage{

    @EmbeddedId
    private ResultMuscleGroupPercentageId id;

    @ManyToOne
    @MapsId("idWorkoutResult")
    @JoinColumn(name = "id_workout_result")
    private WorkoutResult workoutResult;

    @ManyToOne
    @MapsId("idMuscleGroup")
    @JoinColumn(name = "id_muscle_group")
    private MuscleGroup muscleGroup;

    private Double percentage;

}
