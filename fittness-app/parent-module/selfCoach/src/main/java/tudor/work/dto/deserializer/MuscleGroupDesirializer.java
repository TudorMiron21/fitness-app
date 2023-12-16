package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import tudor.work.model.MuscleGroup;
import tudor.work.service.MuscleGroupService;

import java.io.IOException;

public class MuscleGroupDesirializer extends StdDeserializer<MuscleGroup> {

    private final MuscleGroupService muscleGroupService;
    public MuscleGroupDesirializer(MuscleGroupService muscleGroupService){
        super(MuscleGroup.class);
        this.muscleGroupService = muscleGroupService;
    }

    @Override
    public MuscleGroup deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String muscleGroupName = node.asText();

        MuscleGroup muscleGroup;

        try{
            muscleGroup = muscleGroupService.getMuscleGroupByName(muscleGroupName);
        } catch (NotFoundException e) {
            throw new RuntimeException(e);
        }
        return muscleGroup;

    }
}
