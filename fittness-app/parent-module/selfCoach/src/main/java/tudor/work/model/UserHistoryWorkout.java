package tudor.work.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
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
    @JsonIgnore
    Set<UserHistoryModule> userHistoryModules;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

}
