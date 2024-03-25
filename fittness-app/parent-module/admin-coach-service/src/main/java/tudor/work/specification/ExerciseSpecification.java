package tudor.work.specification;

import lombok.RequiredArgsConstructor;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import tudor.work.model.Exercise;
import java.util.Set;

@Component
@RequiredArgsConstructor
public class ExerciseSpecification {
    public static Specification<Exercise> nameLike(String name) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.like(root.get("name"), "%" + name + "%");
    }

    public static Specification<Exercise> muscleGroupNameIn(Set<String> muscleGroupNames) {
        return (root, query, criteriaBuilder) -> root.get("muscleGroup").get("name").in(muscleGroupNames);
    }

    public static Specification<Exercise> categoryNameIn(Set<String> categoryNames) {
        return (root, query, criteriaBuilder) -> root.get("category").get("name").in(categoryNames);
    }

    public static Specification<Exercise> difficultyNameIn(Set<String> difficultyNames) {
        return (root, query, criteriaBuilder) -> root.get("difficulty").get("dificultyLevel").in(difficultyNames);
    }

    public static Specification<Exercise> equipmentNameIn(Set<String> equipmentNames) {
        return (root, query, criteriaBuilder) -> root.get("equipment").get("name").in(equipmentNames);
    }

    public static Specification<Exercise> isExercisePrivateEqual(Boolean isExercisePrivate) {
        return (root, query, criteriaBuilder) -> criteriaBuilder.equal(root.get("isExerciseExclusive"), isExercisePrivate);
    }

    public static Specification<Exercise> isExerciseAdderEqual(Long exerciseAdderId) {
        return (root, query, criteriaBuilder) ->
                criteriaBuilder.equal(root.get("adder"), exerciseAdderId);
    }

}
