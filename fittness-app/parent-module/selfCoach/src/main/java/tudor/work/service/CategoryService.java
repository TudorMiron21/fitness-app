package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Category;
import tudor.work.repository.CategoryRepository;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;

    public Category getCategoryByName(String name) throws NotFoundException
    {
        return categoryRepository.findByName(name).orElseThrow(()->new NotFoundException("category not found"));
    }
}
