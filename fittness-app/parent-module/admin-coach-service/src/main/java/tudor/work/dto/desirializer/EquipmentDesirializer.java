package tudor.work.dto.desirializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import tudor.work.model.Equipment;

import java.io.IOException;

//public class EquipmentDesirializer extends StdDeserializer<Equipment> {
//
//
//    private final EquipmentService equipmentService;
//
//    public EquipmentDesirializer(EquipmentService equipmentService) {
//        super(Equipment.class);
//        this.equipmentService = equipmentService;
//    }
//
//
//    @Override
//    public Equipment deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
//        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
//        String equipmentName = node.asText();
//
//        Equipment equipment;
//
//        try {
//            equipment = equipmentService.getEquipmentByName(equipmentName);
//        } catch (NotFoundException e) {
//            throw new RuntimeException(e);
//        }
//        return equipment;
//
//    }
//
//}