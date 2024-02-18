package tudor.work.model;

import lombok.*;
import org.intellij.lang.annotations.JdkConstants;
import org.springframework.jmx.export.annotation.ManagedNotifications;

import javax.persistence.*;
import java.util.Set;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
@Entity
@Table
public class WorkoutResult {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long totalTime;

    private Double totalCaloriesBurned;


    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_userHistoryWorkout")
    private UserHistoryWorkout userHistoryWorkout;


    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ResultCategoryPercentage> resultCategoryPercentages;


    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ResultMuscleGroupPercentage> resultMuscleGroupPercentages;

    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ResultDifficultyPercentage> resultDifficultyPercentages;


    private Double workoutDifficulty;


    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    private Set<ResultExercisePersonalRecord> personalRecords;

}


