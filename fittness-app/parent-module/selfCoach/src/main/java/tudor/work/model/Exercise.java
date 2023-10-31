package tudor.work.model;


import com.google.inject.BindingAnnotation;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;


@RequiredArgsConstructor
@Data
@Entity
@Builder
@Table(name = "exercise")
@AllArgsConstructor
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private String mediaUrl;

//    @ManyToOne
//    @JoinColumn(name = "adder_id")
//    private User adder;

    @ManyToOne
    @JoinColumn(name = "difficulty_id")
    private Difficulty difficulty;

    @ManyToOne
    @JoinColumn(name = "category_id")
    private Category category;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {
            CascadeType.PERSIST,
            CascadeType.MERGE,
            CascadeType.REMOVE
    },mappedBy = "exercises")
    @JsonIgnore
    private Set<Workout> workouts;


    private boolean isExerciseExclusive;

}
