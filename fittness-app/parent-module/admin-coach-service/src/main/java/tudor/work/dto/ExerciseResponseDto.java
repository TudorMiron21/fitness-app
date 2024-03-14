package tudor.work.dto;


import lombok.*;
import tudor.work.model.Category;
import tudor.work.model.Difficulty;
import tudor.work.model.Equipment;
import tudor.work.model.MuscleGroup;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseResponseDto {

    private Long exerciseId;

    private String name;

    private String description;

    private String exerciseImageStartUrl;

    private MuscleGroup muscleGroup;

    private Equipment equipment;

    private Difficulty difficulty;

    private Category category;

    private Boolean isExerciseExclusive;

}
