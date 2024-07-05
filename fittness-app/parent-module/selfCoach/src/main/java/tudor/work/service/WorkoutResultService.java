package tudor.work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.expression.spel.ast.OpAnd;
import org.springframework.stereotype.Service;
import tudor.work.model.User;
import tudor.work.model.WorkoutResult;
import tudor.work.repository.WorkoutResultRepository;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@RequiredArgsConstructor
@Service
public class WorkoutResultService {

    private final WorkoutResultRepository workoutResultRepository;

    public WorkoutResult save(WorkoutResult workoutResult)
    {
        return workoutResultRepository.save(workoutResult);
    }
    public WorkoutResult saveAndFlush(WorkoutResult workoutResult)
    {
        return workoutResultRepository.saveAndFlush(workoutResult);
    }
    public Optional<WorkoutResult> findById(Long id)
    {
        return workoutResultRepository.findById(id);
    }

    public List<WorkoutResult> findByUserEmail(String userEmail) {
        return workoutResultRepository.findByUserEmail(userEmail);
    }

    public WorkoutResult findLastByUser(String userEmail) {

        List<WorkoutResult>  workoutResultsByEmail = findByUserEmail(userEmail);

        if(!workoutResultsByEmail.isEmpty())
            return workoutResultsByEmail.get(workoutResultsByEmail.size() -1);

        return null;
    }

    public List<WorkoutResult> findLastEntriesByUser(String userEmail,Integer noWorkoutResults){
        List<WorkoutResult>  workoutResultsByEmail = findByUserEmail(userEmail);

        workoutResultsByEmail = workoutResultsByEmail.stream().map(this::transformNullFields).toList();
        Integer workoutResultsByEmailCount = workoutResultsByEmail.size();

        if(!workoutResultsByEmail.isEmpty())
        {
            if(workoutResultsByEmailCount >= noWorkoutResults)
                return workoutResultsByEmail.subList(workoutResultsByEmailCount - noWorkoutResults,workoutResultsByEmailCount);
            else
                return workoutResultsByEmail;
        }
        return  Collections.emptyList();

    }

    public WorkoutResult transformNullFields(WorkoutResult workoutResult){
        if(workoutResult.getTotalTime() == null)
            workoutResult.setTotalTime(0L);
        if(workoutResult.getTotalVolume() == null)
            workoutResult.setTotalVolume((double)0);
        if(workoutResult.getTotalCaloriesBurned() == null)
            workoutResult.setTotalCaloriesBurned((double)0);
        return workoutResult;
    }

}
