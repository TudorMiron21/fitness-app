package tudor.work.dto;

import lombok.*;

@Builder
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class FilterSearchDto {

    private String name;

    private Double maxDifficulty;

    private Double minDifficulty;
}
