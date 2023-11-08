package tudor.work.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.persistence.*;

@Entity
@Table
@Data
@EqualsAndHashCode
@Builder
public class UserHistoryExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_userHistoryModule")
    private UserHistoryModule userHistoryModule;

    //this represents the current number of seconds
    private Long currNoSeconds;

    private boolean isDone;

    private boolean isPaused;

    private Integer noReps;

    private Double weight;

}
