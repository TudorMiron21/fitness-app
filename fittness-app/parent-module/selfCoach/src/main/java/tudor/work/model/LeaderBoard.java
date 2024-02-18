package tudor.work.model;

import lombok.Getter;

import javax.persistence.*;

@Entity

public class LeaderBoard {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

//    @OneToOne
//    private User user;

    private Integer totalNumberOfPoints;

    
}
