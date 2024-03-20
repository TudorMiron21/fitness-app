package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.desirializer.SetLongJsonDeserializer;

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

    @JsonDeserialize(using = SetLongJsonDeserializer.class)
    private Set<Long> exerciseIds;

}
