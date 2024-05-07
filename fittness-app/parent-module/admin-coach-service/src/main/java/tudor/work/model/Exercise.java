package tudor.work.model;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.*;

import javax.persistence.*;

@RequiredArgsConstructor
//@Data
@Getter
@Setter
@Entity
@Builder
@Table(name = "exercise")
@AllArgsConstructor
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

    @ManyToOne
    @JoinColumn(name = "adder_id")
    @JsonIgnore
    private User adder;

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


    //this is on true if this exercise is added by a coach;
    private boolean isExerciseExclusive;

    private boolean hasWeight;

    private boolean hasNoReps;

}