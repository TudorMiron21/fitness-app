package tudor.work.dto;


import lombok.Data;
import tudor.work.model.Exercise;

import java.util.Set;

@Data

public class PostWorkoutRequestDto {

    private String name;
    private String description;
    private Set<Exercise> exercises;
}
