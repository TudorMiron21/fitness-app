package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.MuscleGroup;
import tudor.work.repository.MuscleGroupRepository;

@RequiredArgsConstructor
@Service
public class MuscleGroupService {

    private final MuscleGroupRepository muscleGroupRepository;

    public MuscleGroup getMuscleGroupByName(String name) throws NotFoundException {
        return muscleGroupRepository.getByName(name).orElseThrow(()->new NotFoundException("Muscle Group not found"));
    }
}
