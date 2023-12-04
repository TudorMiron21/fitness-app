package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.JsonDeserializer;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import lombok.SneakyThrows;
import tudor.work.model.UserHistoryModule;
import tudor.work.service.UserHistoryModuleService;

import java.io.IOException;

public class UserHistoryModuleDeserializer extends StdDeserializer<UserHistoryModule> {


    private final UserHistoryModuleService userHistoryModuleService;

    protected UserHistoryModuleDeserializer(UserHistoryModuleService userHistoryModuleService) {
        super(UserHistoryModuleDeserializer.class);
        this.userHistoryModuleService = userHistoryModuleService;
    }

    @SneakyThrows(NotFoundException.class)
    @Override
    public UserHistoryModule deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {

        JsonNode node = jsonParser.getCodec().readTree(jsonParser);

        return userHistoryModuleService.getModuleById(node.asLong());


    }
}
