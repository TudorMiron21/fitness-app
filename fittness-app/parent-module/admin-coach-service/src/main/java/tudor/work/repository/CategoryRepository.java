package tudor.work.repository;

import org.springframework.stereotype.Repository;
import tudor.work.model.Category;

import java.util.Optional;


@Repository

public interface CategoryRepository {
    Optional<Category> findByName(String categoryName);
}
