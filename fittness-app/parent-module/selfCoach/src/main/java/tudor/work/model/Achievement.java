package tudor.work.model;


import javax.persistence.*;

@Entity
@Table
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long name;

    private Long description;

    private Integer numberOfPoints;

}
