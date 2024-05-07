package tudor.work.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProgramDto {
    private Long id;
    private String name;
    private Double difficultyLevel;
    private String coverPhotoUrl;
}
