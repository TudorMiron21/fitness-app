package tudor.work.model;

import lombok.*;
import tudor.work.embeddables.ResultDifficultyPercentageId;

import javax.persistence.*;

@Entity
@Table
@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ResultDifficultyPercentage {

    @EmbeddedId
    private ResultDifficultyPercentageId id;

    @ManyToOne
    @MapsId("idWorkoutResult")
    @JoinColumn(name = "id_workout_result")
    private WorkoutResult workoutResult;

    @ManyToOne
    @MapsId("idDifficulty")
    @JoinColumn(name = "id_difficulty")
    private Difficulty difficulty;

    private double percentage;

}
