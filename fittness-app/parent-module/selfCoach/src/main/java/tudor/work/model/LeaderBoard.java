package tudor.work.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class LeaderBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = "id_user")
    private User user;

    @Column(precision = 10, scale = 2)
    private Double numberOfPoints;

    private Integer numberOfDoneExercises;

    private Integer numberOfDoneWorkouts;

    private Integer numberOfDonePrograms;


}
