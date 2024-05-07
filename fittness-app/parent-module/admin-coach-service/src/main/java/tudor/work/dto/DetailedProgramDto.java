package tudor.work.dto;

import lombok.*;
import tudor.work.model.WorkoutProgram;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedProgramDto {
    private Long id;
    private String name;
    private String description;
    private Double difficultyLevel;
    private Integer durationInDays;
    private String coverPhotoUrl;
    private Set<WorkoutProgram> workoutPrograms;
}
