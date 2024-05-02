package tudor.work.specification;

import org.springframework.data.jpa.domain.Specification;
import tudor.work.model.Program;

public class ProgramSpecification {

    public static Specification<Program> nameLike(String name)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Program> isProgramDifficultyLevelBetween(Double minDifficulty, Double maxDifficulty)
    {
        return  (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("difficultyLevel"),minDifficulty,maxDifficulty);
    }
}
