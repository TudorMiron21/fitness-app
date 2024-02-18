package tudor.work.model;

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
    List<UserHistoryModule> userHistoryModules;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    private Boolean isWorkoutDone;

    //these members are not integrated in the workflow
    private LocalDateTime startedWorkoutDateAndTime;
    private LocalDateTime finishedWorkoutDateAndTime;

    public void addUserHistoryModule(UserHistoryModule userHistoryModule)
    {
        this.userHistoryModules.add(userHistoryModule);
    }

}
