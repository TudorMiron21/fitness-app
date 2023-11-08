package tudor.work.dto;


import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tudor.work.dto.deserializer.ExercisesDeserialiser;
import tudor.work.dto.deserializer.UserDeserializer;
import tudor.work.model.Exercise;
import tudor.work.model.User;

import java.util.Set;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WorkoutDto {

    private String name;
    private String description;
    private String coverPhotoUrl;

    @JsonDeserialize(using = ExercisesDeserialiser.class)
    private Set<Exercise> exercises;


//
//    @JsonDeserialize(using = UserDeserializer.class)
//    private User adder;
}
