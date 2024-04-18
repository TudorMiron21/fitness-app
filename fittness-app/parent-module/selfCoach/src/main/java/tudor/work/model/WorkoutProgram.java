package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.util.List;
import java.util.Set;


@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutProgram {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "id_program")
    @JsonBackReference
    private Program program;

    @ManyToOne
    @JoinColumn(name = "id_workout")
//    @JsonBackReference
    @JsonManagedReference
    private Workout workout;

    private Integer workoutIndex;
}
