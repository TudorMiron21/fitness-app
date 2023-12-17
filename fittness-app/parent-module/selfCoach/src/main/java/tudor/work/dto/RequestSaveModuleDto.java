package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Data;
import tudor.work.model.Exercise;
import tudor.work.model.UserHistoryWorkout;
import tudor.work.model.Workout;

@Data
public class RequestSaveModuleDto {

    private Long parentUserHistoryWorkoutId;

    private Integer noSets;
}
