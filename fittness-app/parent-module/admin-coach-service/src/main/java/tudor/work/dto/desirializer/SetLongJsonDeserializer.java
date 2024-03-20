package tudor.work.dto.desirializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class SetLongJsonDeserializer extends JsonDeserializer<Set<Long>> {

    @Override
    public Set<Long> deserialize(JsonParser jsonParser, DeserializationContext deserializationContext)
            throws IOException, JsonProcessingException {
        Set<Long> resultSet = new HashSet<>();
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String[] ids = node.textValue().split(",");
        for (String id : ids) {
            if (!id.trim().isEmpty()) {
                resultSet.add(Long.parseLong(id.trim()));
            }
        }
        return resultSet;
    }
}