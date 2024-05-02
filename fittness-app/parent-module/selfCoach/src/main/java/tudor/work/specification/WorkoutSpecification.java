package tudor.work.specification;

import org.springframework.data.jpa.domain.Specification;
import tudor.work.model.Workout;

public class WorkoutSpecification {
    public static Specification<Workout> nameLike(String name)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }


    public static Specification<Workout> isWorkoutDifficultyLevelBetween(Double minDifficulty, Double maxDifficulty)
    {
        return  (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("difficultyLevel"),minDifficulty,maxDifficulty);
    }
}
