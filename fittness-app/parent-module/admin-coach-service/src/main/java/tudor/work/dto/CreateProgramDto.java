package tudor.work.dto;


import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProgramDto {

    private String name;

    private String description;

    private Integer durationInDays;

    private Map<Integer, Long> indexedWorkouts;
}


