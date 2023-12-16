package tudor.work.model;


import lombok.Data;

import javax.persistence.*;

@Data
@Entity
@Table(name = "equipment")
public class Equipment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long id;

    private String name;

    //this is a link which contains a list of all exercises that use that particular machine
    private String equipmentDetailsUrl;
}

