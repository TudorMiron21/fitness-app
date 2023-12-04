package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.UserHistoryWorkout;
import tudor.work.repository.UserHistoryWorkoutRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHistoryWorkoutService {

    private final UserHistoryWorkoutRepository userHistoryWorkoutRepository;


    public Optional<UserHistoryWorkout> getUserHistoryWorkout(Long Id)
    {
        return userHistoryWorkoutRepository.findById(Id);
    }


    public UserHistoryWorkout save(UserHistoryWorkout userHistoryWorkout) {

        return userHistoryWorkoutRepository.save(userHistoryWorkout);

    }
    public UserHistoryWorkout saveAndFlush(UserHistoryWorkout userHistoryWorkout) {

        return userHistoryWorkoutRepository.saveAndFlush(userHistoryWorkout);

    }

}
