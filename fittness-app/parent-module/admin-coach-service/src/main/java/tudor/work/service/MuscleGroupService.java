package tudor.work.service;

import javassist.NotFoundException;
import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.MuscleGroup;
import tudor.work.repository.MuscleGroupRepostory;

@Service
@RequiredArgsConstructor
public class MuscleGroupService {

    private final MuscleGroupRepostory muscleGroupRepostory;

    public MuscleGroup getMuscleGroupByName(String muscleGroupName) throws NotFoundException {
        return muscleGroupRepostory.findByName(muscleGroupName).orElseThrow(()-> new NotFoundException("muscle group with name" +muscleGroupName+ "not found"));
    }
}
