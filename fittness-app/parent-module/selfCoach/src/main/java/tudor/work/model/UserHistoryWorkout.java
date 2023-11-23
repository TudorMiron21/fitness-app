package tudor.work.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.List;
import java.util.Set;

@Entity
@Table
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString(exclude = "userHistoryModules")
@EqualsAndHashCode(exclude = "userHistoryModules")
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

    public void addUserHistoryModule(UserHistoryModule userHistoryModule)
    {
        this.userHistoryModules.add(userHistoryModule);
    }

}
