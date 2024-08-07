package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "workout")
//@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder

////This is the fucking solution for my problems
//@ToString(exclude = "likers")
//@EqualsAndHashCode(exclude = "likers")
public class Workout {

    @javax.persistence.Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    private String name;

    @Column(length = 1024)
    private String description;

    private String coverPhotoUrl;

    @ManyToMany(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "Exercise_Workout", joinColumns = @JoinColumn(name = "id_workout"), inverseJoinColumns = @JoinColumn(name = "id_exercise"))
    Set<Exercise> exercises = new HashSet<>();


    @ManyToOne
    @JoinColumn(name = "adder_id")
    @JsonIgnore
    private User adder;


    @ManyToMany(mappedBy = "likedWorkouts")
    @JsonIgnore
    private Set<User> likers;

    @OneToMany(
            mappedBy = "workout",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
//    @JsonManagedReference
    @JsonBackReference
    Set<WorkoutProgram> workoutPrograms;


    private boolean isGlobal;

    private boolean isDeleted = false;

    private Double difficultyLevel;

    public void addExercise(Exercise exercise) {
        this.exercises.add(exercise);
    }

    public void removeExercise(Exercise exercise) {
        this.exercises.remove(exercise);
    }
}
