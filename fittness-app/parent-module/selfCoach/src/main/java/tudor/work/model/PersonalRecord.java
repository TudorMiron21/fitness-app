package tudor.work.model;

import lombok.*;

import javax.persistence.*;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PersonalRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;


    @OneToOne
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    @ManyToOne
    @JoinColumn(name = "id_user")
    private User user;

    private Double maxWeight;

    private Long maxTime;

    private Double maxCalories;

    private Integer maxNoReps;
}
