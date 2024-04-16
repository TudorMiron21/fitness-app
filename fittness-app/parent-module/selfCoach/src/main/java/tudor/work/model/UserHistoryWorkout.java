package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.query.criteria.internal.predicate.BooleanExpressionPredicate;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
//@Data
//@ToString(exclude = "userHistoryModules")
//@EqualsAndHashCode(exclude = "userHistoryModules")
public class UserHistoryWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryWorkout",cascade = CascadeType.ALL)
    @JsonManagedReference
    List<UserHistoryModule> userHistoryModules;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;


    @ManyToOne
    @JoinColumn(name = "id_userHistoryProgram")
    @JsonBackReference
    private UserHistoryProgram userHistoryProgram;

    private Boolean isWorkoutDone;

    private LocalDateTime startedWorkoutDateAndTime;
    private LocalDateTime finishedWorkoutDateAndTime;

    public void addUserHistoryModule(UserHistoryModule userHistoryModule)
    {
        this.userHistoryModules.add(userHistoryModule);
    }

}
