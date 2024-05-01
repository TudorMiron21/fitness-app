package tudor.work.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class LeaderBoardDto {
    private Long id;
    private UserDto user;
    private Double numberOfPoints;
    private Integer numberOfDoneExercises;
    private Integer numberOfDoneWorkouts;
    private Integer numberOfDonePrograms;
}
