package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import tudor.work.model.Difficulty;
import tudor.work.model.User;
import tudor.work.service.DifficultyService;
import tudor.work.service.UserService;

import java.io.IOException;

public class UserDeserializer extends StdDeserializer<User> {

    private final UserService userService;

    public UserDeserializer(UserService userService) {
        super(Difficulty.class);
        this.userService = userService;
    }


    @Override
    public User deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {


        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String username = node.asText();

        User user = new User();

        try {
            user = userService.getUserByName(username);
        }
        catch(NotFoundException nfe)
        {
            return null;
        }
        if (user == null) {
            throw new JsonParseException(jsonParser, "Invalid User: " + username);
        }
        return user;
    }
}
