package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;
import org.springframework.web.multipart.MultipartFile;
import tudor.work.dto.desirializer.CategoryDeserializer;
import tudor.work.dto.desirializer.DifficultyDeserializer;
import tudor.work.dto.desirializer.MuscleGroupDesirializer;
import tudor.work.model.Category;
import tudor.work.model.Difficulty;
import tudor.work.model.Equipment;
import tudor.work.model.MuscleGroup;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UploadExerciseDto {
    private String name;

    private String description;

//    private String exerciseImageStartPath;
//
//    private String exerciseImageEndPath;

    private MultipartFile exerciseVideoFile;

//    private boolean isExerciseExclusive;

//    @JsonDeserialize(using = EquipmentDesirializer.class)
//    private Equipment equipment;
//    @JsonDeserialize(using = MuscleGroupDesirializer.class)
//    private MuscleGroup muscleGroup;
//    @JsonDeserialize(using = DifficultyDeserializer.class)
//    private Difficulty difficulty;
//    @JsonDeserialize(using = CategoryDeserializer.class)
//    private Category category;


}
