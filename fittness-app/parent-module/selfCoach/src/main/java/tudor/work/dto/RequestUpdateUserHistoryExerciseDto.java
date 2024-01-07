package tudor.work.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RequestUpdateUserHistoryExerciseDto {

    private Long currentNoSeconds;

    private Integer noReps;

    private Double weight;

}
