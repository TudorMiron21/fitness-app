package tudor.work.repository;

import org.springframework.stereotype.Repository;
import tudor.work.model.MuscleGroup;

import java.util.Optional;

@Repository
public interface MuscleGroupRepostory {

    Optional<MuscleGroup> findByName(String muscleGroupName);
}
