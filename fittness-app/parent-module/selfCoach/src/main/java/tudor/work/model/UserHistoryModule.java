package tudor.work.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString(exclude = "userHistoryExercises")
@EqualsAndHashCode(exclude = "userHistoryExercises")
@Builder
public class UserHistoryModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryModule",cascade = CascadeType.ALL)
    private List<UserHistoryExercise> userHistoryExercises;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryWorkout")
    private UserHistoryWorkout userHistoryWorkout;

    private Integer noSets;

    public void addExerciseToUserHistoryExercises(UserHistoryExercise userHistoryExercise)
    {
        this.userHistoryExercises.add(userHistoryExercise);
    }

    public void addExercisesToUserHistoryExercises(List<UserHistoryExercise >userHistoryExercises)
    {
        this.userHistoryExercises.addAll(userHistoryExercises);
    }
}
