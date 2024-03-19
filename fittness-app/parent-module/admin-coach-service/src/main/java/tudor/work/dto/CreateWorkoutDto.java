package tudor.work.dto;

import lombok.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.constraints.NotNull;
import java.util.Set;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CreateWorkoutDto {

    private String name;

    private String description;

    private MultipartFile coverPhoto;

    private Set<Long> exerciseIds;

}
