package tudor.work.dto;

import lombok.*;
import tudor.work.model.Equipment;

import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseFilteredRequestDto {

    private String name;

    private Set<String> muscleGroupNames;

    private Set<String> equipmentNames;

    private Set<String> difficultyNames;

    private Set<String> categoryNames;

    private Boolean isExercisePrivate;

}
