package tudor.work.dto;

import lombok.*;
import org.springframework.jmx.export.annotation.ManagedNotifications;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DetailsUserHistoryExerciseDto {

    private Long currentNoSeconds;

    private Integer noReps;

    private Double weight;

}
