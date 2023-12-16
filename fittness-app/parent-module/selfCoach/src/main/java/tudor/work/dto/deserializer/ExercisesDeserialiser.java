package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import org.springframework.data.crossstore.ChangeSetPersister;
import org.springframework.stereotype.Component;
import tudor.work.model.Exercise;
import tudor.work.service.ExerciseService;

import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

@Component
public class ExercisesDeserialiser extends StdDeserializer<Set<Exercise>> {
    private final ExerciseService exerciseService;


    public ExercisesDeserialiser(ExerciseService exerciseService) {
        super(ExercisesDeserialiser.class);
        this.exerciseService = exerciseService;
    }

    @Override
    public Set<Exercise> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        Set<Exercise> exercises = new HashSet<>();

        if (node.isArray()) {
            for (JsonNode exerciseNode : node) {
                Long exerciseId = exerciseNode.asLong();

                Exercise exercise = exerciseService.getExerciseById(exerciseId);
//                if(exercise.isPresent())
//                {
                exercises.add(exercise);
//                }
//                else{
//                    //this is the case in which an exercise is not found in the database
//                    throw new IOException("exercise not found in the database");
//                }
            }
        }
        return exercises;
    }


}
