package tudor.work.model;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.ManyToOne;

public class UserHistoryExercise {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long Id;

    @ManyToOne
    private Exercise exercise;

    @ManyToOne
    private User user;


    @ManyToOne
    private UserHistoryModule userHistoryModule;

    //this represents the number of current number of seconds
    private Long currNoSeconds;


    private boolean isDone;

    private boolean isPaused;

    private Integer noReps;

    private Double weight;

}
