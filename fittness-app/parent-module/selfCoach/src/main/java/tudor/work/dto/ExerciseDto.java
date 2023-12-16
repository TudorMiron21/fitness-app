package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.netflix.discovery.util.EurekaEntityComparators;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import tudor.work.dto.deserializer.CategoryDeserializer;
import tudor.work.dto.deserializer.DifficultyDeserializer;
import tudor.work.dto.deserializer.EquipmentDesirializer;
import tudor.work.dto.deserializer.MuscleGroupDesirializer;
import tudor.work.model.*;

import javax.persistence.*;
import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ExerciseDto {
    private String name;

    private String description;

    private String mediaUrl;

//    private String coverPhotoUrl;

    private String exerciseImageStartUrl;

    private String exerciseImageEndUrl;

    private String exerciseVideoUrl;


    //this field indicates if the exercise has been added by a coach or teh admin(isExclusive == true ==> has been added by coach)
    private boolean isExerciseExclusive;

    @JsonDeserialize(using = EquipmentDesirializer.class)
    private Equipment equipment;

    @JsonDeserialize(using = MuscleGroupDesirializer.class)
    private MuscleGroup muscleGroup;

    @JsonDeserialize(using = DifficultyDeserializer.class)
    private Difficulty difficulty;
    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    private Set<Workout> workouts;

}
