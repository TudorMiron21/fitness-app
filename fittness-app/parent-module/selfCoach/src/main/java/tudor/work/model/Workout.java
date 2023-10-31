package tudor.work.model;

import lombok.Builder;
import lombok.Data;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
@Data
@Builder
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String description;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JoinTable(name = "Exercise_Workout", joinColumns = @JoinColumn(name = "id_workout"), inverseJoinColumns = @JoinColumn(name = "id_exercise"))
    Set<Exercise> exercises = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "adder_id")
    private User adder;
    private boolean isGlobal;

    private boolean isDeleted = false;
}
