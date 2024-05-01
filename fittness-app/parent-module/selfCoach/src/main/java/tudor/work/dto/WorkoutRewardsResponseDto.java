package tudor.work.dto;


import lombok.*;
import tudor.work.model.Achievement;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class WorkoutRewardsResponseDto {

    private Double numberOfPoints;
    private Set<AchievementDto> achievements;

}
