package tudor.work.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ExerciseDetailsResponse {

    private Boolean hasReps;
    private Boolean hasDistance;
    private Boolean hasWeight;
}
