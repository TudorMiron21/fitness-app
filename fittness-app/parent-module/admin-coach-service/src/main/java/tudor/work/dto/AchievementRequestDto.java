package tudor.work.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementRequestDto {

    private String name;

    private Integer numberOfExercisesMilestone;

    private Integer numberOfWorkoutsMileStone;

    private Integer numberOfProgramsMilestone;

    private Integer numberOfChallengesMilestone;

    private String description;

    private Double numberOfPoints;

    private MultipartFile achievementPicture;

}
