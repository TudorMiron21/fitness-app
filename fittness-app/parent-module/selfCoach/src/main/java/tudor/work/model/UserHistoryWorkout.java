package tudor.work.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;
import java.util.Set;

@Entity
@Table
@Data
@EqualsAndHashCode
@Builder
public class UserHistoryWorkout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "workout_id")
    private Workout workout;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryWorkout")
    Set<UserHistoryModule> userHistoryModules;

    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

}
