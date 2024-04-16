package tudor.work.dto;

import lombok.*;
import tudor.work.model.WorkoutProgram;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramDto {
    private Long id;
    private String name;
    private Integer durationInDays;
    private String coverPhotoUrl;
    private Set<WorkoutProgram> workoutProgramSet;
}
