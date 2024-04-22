package tudor.work.dto;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CoachDetailsDto {
    private Integer numberOfSubscribers;

    private Integer numberOfExercises;

    private Integer numberOfWorkouts;

    private Integer numberOfPrograms;

    private Integer numberOfChallenges;
}
