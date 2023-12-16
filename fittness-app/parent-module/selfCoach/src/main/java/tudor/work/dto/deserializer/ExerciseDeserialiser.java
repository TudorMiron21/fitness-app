package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import tudor.work.model.Exercise;
import tudor.work.service.ExerciseService;

import java.io.IOException;


@Component
public class ExerciseDeserialiser extends StdDeserializer<Exercise> {

    private final ExerciseService exerciseService;

    public ExerciseDeserialiser(ExerciseService exerciseService) {
        super(Exercise.class);
        this.exerciseService = exerciseService;
    }
    @Override
    public Exercise deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        return exerciseService.getExerciseById(node.asLong());

    }
}
