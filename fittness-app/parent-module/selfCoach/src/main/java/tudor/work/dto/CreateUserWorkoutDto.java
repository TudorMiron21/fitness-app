package tudor.work.dto;

import lombok.*;

import java.util.List;

@Setter
@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreateUserWorkoutDto {
    private String workoutName;
    private String description;


    private List<Long> exercisesIds;

}
