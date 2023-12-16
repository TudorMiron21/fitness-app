package tudor.work.dto.deserializer;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;
import javassist.NotFoundException;
import org.springframework.stereotype.Component;
import tudor.work.model.Category;
import tudor.work.service.CategoryService;

import java.io.IOException;


@Component
public class CategoryDeserializer extends StdDeserializer<Category> {

    private final CategoryService categoryService;


    public CategoryDeserializer(CategoryService categoryService) {
        super(Category.class);
        this.categoryService = categoryService;
    }


    @Override
    public Category deserialize(JsonParser jsonParser, DeserializationContext deserializationContext) throws IOException, JsonProcessingException {
        JsonNode node = jsonParser.getCodec().readTree(jsonParser);
        String categoryName = node.asText();

        Category category= new Category();
        try {
            category = categoryService.getCategoryByName(categoryName);
        }
        catch(NotFoundException nfe){
            throw new RuntimeException(nfe);
        }

        return category;
    }


}
