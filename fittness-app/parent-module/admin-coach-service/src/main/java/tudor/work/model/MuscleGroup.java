package tudor.work.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "muscle_group")
public class MuscleGroup {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;



    private String muscleGroupDetailsUrl;

}
