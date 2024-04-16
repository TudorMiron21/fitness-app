package tudor.work.model;


import com.fasterxml.jackson.annotation.JsonBackReference;

import javax.persistence.*;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table
public class Achievement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long name;

    private Long description;

    private Integer numberOfPoints;

    private String achievementPicturePath;

    @ManyToMany(mappedBy = "achievements",cascade = CascadeType.ALL)
    @JsonBackReference
    private Set<User> users;

}
