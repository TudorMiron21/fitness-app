package tudor.work.dto;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import tudor.work.dto.deserializer.ExerciseDeserialiser;
import tudor.work.dto.deserializer.UserHistoryModuleDeserializer;
import tudor.work.model.Exercise;
import tudor.work.model.UserHistoryModule;


@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RequestUserHistoryExercise {


    @JsonDeserialize(using = ExerciseDeserialiser.class)
    private Exercise exercise;

    @JsonDeserialize(using = UserHistoryModuleDeserializer.class)
    private UserHistoryModule userHistoryModule;

    private Long currNoSeconds;

    private boolean isDone;

    private Integer noReps;

    private Double weight;//in kg

    private Double distance;// in meters
}
