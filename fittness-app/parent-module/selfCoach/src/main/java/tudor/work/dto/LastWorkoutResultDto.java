package tudor.work.dto;


import lombok.*;
import tudor.work.model.ResultCategoryPercentage;
import tudor.work.model.ResultDifficultyPercentage;
import tudor.work.model.ResultMuscleGroupPercentage;

import java.io.Serial;
import java.util.Set;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class LastWorkoutResultDto {

    private Long id;

    private Long totalTime;

    private Double totalCaloriesBurned;

    private Double totalVolume;

    private Set<ResultCategoryPercentage> resultCategoryPercentages;

    private Set<ResultDifficultyPercentage> resultDifficultyPercentages;

    private Set<ResultMuscleGroupPercentage> resultMuscleGroupPercentages;
}
