package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import lombok.*;

import javax.persistence.*;
import java.util.Set;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;

    private Integer numberOfExercisesMilestone;

    private Integer numberOfWorkoutsMileStone;

    private Integer numberOfProgramsMilestone;

    private Integer numberOfChallengesMilestone;

    private String description;

    @Column(precision = 10, scale = 2)
    private Double numberOfPoints;

    private String achievementPicturePath;

    @ManyToMany(mappedBy = "achievements",cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<User> users;

}
