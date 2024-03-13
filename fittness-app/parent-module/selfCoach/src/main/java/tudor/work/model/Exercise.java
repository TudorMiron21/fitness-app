package tudor.work.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;


@RequiredArgsConstructor
@Data
@Entity
@Builder
@Table(name = "exercise")
@AllArgsConstructor
@EqualsAndHashCode
@ToString
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    @Column(nullable = false)
    private String name;

    @Column(length = 1024)
    private String description;

    private String descriptionUrl;

    private String exerciseImageStartUrl;

    private String exerciseImageEndUrl;

    private String exerciseVideoUrl;

    //private String coverPhotoUrl;

    @ManyToOne
    @JoinColumn(name ="muscle_group_id")
    private MuscleGroup muscleGroup;

//    @ManyToOne
//    @JoinColumn(name = "adder_id")
//    private User adder;

    @ManyToOne
    @JoinColumn(name ="equipment_id")
    private Equipment equipment;


    private Double rating;


    @ManyToOne
    @JoinColumn(name = "difficulty_id")
    private Difficulty difficulty;


    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    @ManyToMany(mappedBy = "exercises")
//    @JsonIgnore
//    private Set<Workout> workouts;


    //this is on true if this exercise is added by a coach;
    private boolean isExerciseExclusive;

    private boolean hasWeight;

    private boolean hasNoReps;

}
