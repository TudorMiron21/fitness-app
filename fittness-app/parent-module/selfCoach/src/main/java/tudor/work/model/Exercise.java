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


    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private String mediaUrl;

    private String coverPhotoUrl;

//    @ManyToOne
//    @JoinColumn(name = "adder_id")
//    private User adder;


    @ManyToOne
    @JoinColumn(name = "difficulty_id")
    private Difficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

//    @ManyToMany(mappedBy = "exercises")
//    @JsonIgnore
//    private Set<Workout> workouts;


    private boolean isExerciseExclusive;

    private boolean hasWeight;

    private boolean hasDistance;

    private boolean hasNoReps;

}
