package tudor.work.model;

import lombok.Data;

import javax.persistence.*;

@Entity
@Table
@Data
public class Difficulty {


    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String dificultyLevel;

    private Double difficultyLevelNumber;
}
