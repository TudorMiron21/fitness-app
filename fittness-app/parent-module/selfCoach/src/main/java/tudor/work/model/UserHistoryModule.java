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
public class UserHistoryModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryModule")
    private Set<UserHistoryExercise> userHistoryExercises;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryWorkout")
    private UserHistoryWorkout userHistoryWorkout;

    private Integer noSets;

}
