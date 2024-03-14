package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;
import tudor.work.dto.ExerciseFilteredRequestDto;
import tudor.work.dto.ExerciseResponseDto;
import tudor.work.model.Exercise;

import java.util.Set;

@Repository
public interface ExerciseRepository extends JpaRepository<Exercise,Long>, JpaSpecificationExecutor<Exercise> {
    Exercise save(Exercise exercise);

    Set<Exercise> findAllByAdderId(Long adderId);

}
