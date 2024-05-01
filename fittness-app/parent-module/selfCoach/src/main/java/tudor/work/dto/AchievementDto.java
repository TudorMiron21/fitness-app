package tudor.work.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AchievementDto {

    private Long id;
    private String name;
    private String description;
    private Double numberOfPoints;
    private String achievementPicturePath;

}
