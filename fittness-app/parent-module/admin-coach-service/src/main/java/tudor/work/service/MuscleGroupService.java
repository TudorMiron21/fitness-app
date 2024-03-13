package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.MuscleGroup;
import tudor.work.repository.MuscleGroupRepository;

@Service
@RequiredArgsConstructor
public class MuscleGroupService {

    private final MuscleGroupRepository muscleGroupRepository;

    public MuscleGroup getMuscleGroupByName(String muscleGroupName) throws NotFoundException {
        return muscleGroupRepository.findByName(muscleGroupName).orElseThrow(()-> new NotFoundException("muscle group with name" +muscleGroupName+ "not found"));
    }
}
