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
@ToString(exclude = "userHistoryExercises")
@EqualsAndHashCode(exclude = "userHistoryExercises")
public class UserHistoryModule {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryModule",cascade = CascadeType.ALL)
    @JsonIgnore
    private Set<UserHistoryExercise> userHistoryExercises;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryWorkout")
    private UserHistoryWorkout userHistoryWorkout;

    private Integer noSets;

}
