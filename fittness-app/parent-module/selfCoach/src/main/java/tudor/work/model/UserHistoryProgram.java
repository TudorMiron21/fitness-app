package tudor.work.model;


import com.fasterxml.jackson.annotation.JsonManagedReference;
import lombok.*;

import javax.persistence.*;
import java.time.LocalDateTime;
import java.util.List;

@Table
@Entity
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserHistoryProgram {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "program_id")
    private Program program;


    @ManyToOne
    @JoinColumn(name ="user_id")
    private User user;

    private Integer currentWorkoutIndex;

    @OneToMany(fetch = FetchType.LAZY,mappedBy = "userHistoryProgram",cascade = CascadeType.ALL)
    @JsonManagedReference
    List<UserHistoryWorkout> userHistoryWorkouts;

    private LocalDateTime startedWorkoutDateAndTime;
    private LocalDateTime finishedWorkoutDateAndTime;

    private Boolean isProgramDone;
}
