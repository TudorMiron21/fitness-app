package tudor.work.dto;

import lombok.*;
import org.springframework.boot.test.autoconfigure.orm.jpa.AutoConfigureTestEntityManager;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class ResponseWorkoutPresentInUserHistoryDto {

    private Long userHistoryModuleId;

    private Long userHistoryExerciseId;

    private Long userHistoryWorkoutId;

    private Integer exerciseIndex;

    private Integer moduleIndex;

    private Integer noSetsLastModule;

}
