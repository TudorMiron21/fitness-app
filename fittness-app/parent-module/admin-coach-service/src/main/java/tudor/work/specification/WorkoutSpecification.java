package tudor.work.specification;


import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tudor.work.model.Workout;

import java.awt.*;

@Component
public class WorkoutSpecification {


    public static Specification<Workout> nameLike(String name)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static  Specification<Workout> isWorkoutPrivateEqual(Boolean isWorkoutPrivate)
    {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isGlobal"),!isWorkoutPrivate);
    }

    public static  Specification<Workout> isWorkoutAdderEqual(Long workoutAdderId){
        return (root, query, criteriaBuilder) ->  criteriaBuilder.equal(root.get("adder"),workoutAdderId);
    }

    public static Specification<Workout> isWorkoutDifficultyLevelBetween(Double minDifficulty, Double maxDifficulty)
    {
        return  (root, query, criteriaBuilder) -> criteriaBuilder.between(root.get("difficultyLevel"),minDifficulty,maxDifficulty);
    }
}


