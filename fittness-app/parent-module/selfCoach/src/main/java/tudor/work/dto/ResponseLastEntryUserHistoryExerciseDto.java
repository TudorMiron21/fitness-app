package tudor.work.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Builder
public class ResponseLastEntryUserHistoryExerciseDto {

    private Boolean isFirstExercise;

    private Boolean isExerciseDone;

    private Long userHistoryExerciseId;

    private Long userHistoryModuleId;
}
