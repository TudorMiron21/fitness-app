package tudor.work.dto;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DetailedWorkoutDto {
    private Long id;
    private String name;
    private String description;

    private Double difficultyLevel;
    private String coverPhotoUrl;
    private List<ExerciseResponseDto> exercises;
}
