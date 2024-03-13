package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Category;

import java.util.Optional;


@Repository

public interface CategoryRepository extends JpaRepository<Category,Long> {
    Optional<Category> findByName(String categoryName);
}
