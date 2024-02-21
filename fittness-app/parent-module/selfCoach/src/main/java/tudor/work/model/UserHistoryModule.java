package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
//@Data
//@ToString(exclude = "userHistoryExercises")
//@EqualsAndHashCode(exclude = "userHistoryExercises")
@Builder
public class UserHistoryModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToMany(fetch = FetchType.EAGER,mappedBy = "userHistoryModule",cascade = CascadeType.ALL)
    @JsonManagedReference
    private List<UserHistoryExercise> userHistoryExercises;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryWorkout")
    @JsonBackReference
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
