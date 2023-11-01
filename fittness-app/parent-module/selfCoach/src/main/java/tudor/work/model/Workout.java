package tudor.work.model;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode
public class Workout {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    private String description;

    @ManyToMany()
    @JoinTable(name = "Exercise_Workout", joinColumns = @JoinColumn(name = "id_workout"), inverseJoinColumns = @JoinColumn(name = "id_exercise"))
    Set<Exercise> exercises = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "adder_id")
    private User adder;
    private boolean isGlobal;

    private boolean isDeleted = false;

    public void addExercise(Exercise exercise)
    {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise)
    {
        this.exercises.remove(exercise);
    }
}
