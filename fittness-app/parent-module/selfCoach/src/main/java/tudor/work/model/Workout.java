package tudor.work.model;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    })
    @JoinTable(name = "Exercise_Workout", joinColumns = @JoinColumn(name = "id_workout"), inverseJoinColumns = @JoinColumn(name = "id_exercise"))
    Set<Exercise> exercises = new HashSet<>();
}
