package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
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
public class Program {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String name;

    @Column(length = 1024)
    private String description;

    private Integer durationInDays;
    private String coverPhotoUrl;

    @OneToMany(
            mappedBy = "program",
            cascade = CascadeType.ALL,
            orphanRemoval = true
    )
    @JsonManagedReference
    private Set<WorkoutProgram> workoutPrograms;

    @ManyToOne
    @JoinColumn(name = "adder_id")
    private User adder;

    private Double difficultyLevel;

    private Boolean isGlobal;
}
