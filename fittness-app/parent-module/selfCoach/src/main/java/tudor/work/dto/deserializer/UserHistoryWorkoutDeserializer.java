package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import tudor.work.model.UserHistoryWorkout;
import tudor.work.service.ExerciseService;
import tudor.work.service.UserHistoryWorkoutService;

import java.io.IOException;

public class UserHistoryWorkoutDeserializer extends StdDeserializer<UserHistoryWorkout> {

    UserHistoryWorkoutService userHistoryWorkoutService;

    public UserHistoryWorkoutDeserializer(UserHistoryWorkoutService userHistoryWorkoutService) {
        super(ExercisesDeserialiser.class);
        this.userHistoryWorkoutService = userHistoryWorkoutService;
    }

    @Override
    public UserHistoryWorkout deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        return null;
    }
}
