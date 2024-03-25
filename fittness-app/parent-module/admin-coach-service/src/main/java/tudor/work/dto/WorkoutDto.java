package tudor.work.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutDto {
    private Long Id;

    private String name;

    private Double difficultyLevel;

    private String coverPhotoUrl;

}
