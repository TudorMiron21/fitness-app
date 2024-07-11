package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHistoryExercise{
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryModule")
    @JsonBackReference
    private UserHistoryModule userHistoryModule;

    //this represents the current number of seconds
    private Long currNoSeconds;

    private boolean isDone;

    private Integer noReps;

    private Double weight;//in kg

    private Double distance;// in meters

    private Double caloriesBurned;
}
