package tudor.work.model;


import lombok.Data;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

@Entity
@Table
@Data
public class Difficulty {


    @Id
    private Long id;

    @Column(nullable = false)
    private String dificultyLevel;
}
