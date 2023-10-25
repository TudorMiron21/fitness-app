package tudor.work.model;


import lombok.Data;
import lombok.RequiredArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.Set;


@RequiredArgsConstructor
@Data
@Entity
@Table(name = "exercise")
public class Exercise {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private String description;

    private String mediaUrl;
//
//    //this field indicates if the exercise has been added by a coach or teh admin(isExclusive == true ==> has been added by coach)
//    private Boolean isExclusive;

    //the id of the coach/admin that has added the exercise
    @ManyToOne
    @JoinColumn(name = "adder_id")
    private User adder;

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

}
