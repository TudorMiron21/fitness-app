package tudor.work.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutFilteredRequestDto {

    private String name;

    private Boolean isWorkoutPrivate;

    private Double maxDifficulty;

    private Double minDifficulty;
}
