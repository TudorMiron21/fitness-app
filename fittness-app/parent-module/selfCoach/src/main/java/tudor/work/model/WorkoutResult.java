package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
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

    private Double totalVolume;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_userHistoryWorkout")
    @JsonIgnore
    private UserHistoryWorkout userHistoryWorkout;


    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ResultCategoryPercentage> resultCategoryPercentages;


    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ResultMuscleGroupPercentage> resultMuscleGroupPercentages;

    @OneToMany(
            mappedBy = "workoutResult",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<ResultDifficultyPercentage> resultDifficultyPercentages;


    private Double workoutDifficulty;


//    @OneToMany(
//            mappedBy = "workoutResult",
//            cascade = CascadeType.ALL,
//            orphanRemoval = true
//    )
//    private Set<ResultExercisePersonalRecord> personalRecords;

}


