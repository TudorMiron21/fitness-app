package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.UserHistoryExercise;
import tudor.work.repository.UserHistoryExerciseRepository;

@Service
@RequiredArgsConstructor
public class UserHistoryExerciseService {

    private final UserHistoryExerciseRepository userHistoryExerciseRepository;

    public UserHistoryExercise findById(Long userHistoryExerciseId) throws NotFoundException {
        return userHistoryExerciseRepository.findById(userHistoryExerciseId).orElseThrow(()->new NotFoundException("user history exercise entry with id "+userHistoryExerciseId+" does not exist"));
    }

}
