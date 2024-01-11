package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedExerciseDto {

    private Long idExercise;
    private String name;
    private String exerciseImageStartUrl;
//    private String equipmentName;
    private String muscleGroupName;
    private String difficultyName;
    private String categoryName;
}
