package tudor.work.dto.desirializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import tudor.work.model.Difficulty;
import tudor.work.service.DifficultyService;

import java.io.IOException;

@Component
public class DifficultyDeserializer extends StdDeserializer<Difficulty> {

    private final DifficultyService difficultyService;


    public DifficultyDeserializer(DifficultyService difficultyService) {
        super(Difficulty.class);
        this.difficultyService = difficultyService;
    }


    @Override
    public Difficulty deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String difficultyName = node.asText();

        // Here, you would typically query your database to retrieve the Difficulty entity
        // based on the name or identifier. Replace this with your actual logic.

        Difficulty difficulty = new Difficulty();
        try {
            difficulty = difficultyService.getDifficultyByName(difficultyName);
        } catch (NotFoundException nfe) {
            return null;
        }

        if (difficulty == null) {
            throw new JsonParseException(jsonParser, "Invalid Difficulty: " + difficultyName);
        }

        return difficulty;
    }


}