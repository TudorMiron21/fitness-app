package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import net.minidev.json.annotate.JsonIgnore;
import tudor.work.dto.deserializer.CategoryDeserializer;
import tudor.work.dto.deserializer.DifficultyDeserializer;
import tudor.work.model.Category;
import tudor.work.model.Difficulty;
import tudor.work.model.User;
import tudor.work.model.Workout;

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
    //
//    //this field indicates if the exercise has been added by a coach or teh admin(isExclusive == true ==> has been added by coach)
    private boolean isExerciseExclusive;

    @JsonDeserialize(using = DifficultyDeserializer.class)
    private Difficulty difficulty;
    @JsonDeserialize(using = CategoryDeserializer.class)
    private Category category;

    private Set<Workout> workouts;

}
