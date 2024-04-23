package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.PersonalRecord;
import tudor.work.model.UserHistoryExercise;
import tudor.work.repository.PersonalRecordsRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class PersonalRecordService {

    private final PersonalRecordsRepository personalRecordsRepository;

    public List<PersonalRecord> findByUserEmail(String userEmail)
    {
        return personalRecordsRepository.findByUserEmail(userEmail);
    }

    public PersonalRecord save(PersonalRecord personalRecord)
    {
        return personalRecordsRepository.save(personalRecord);
    }

    public Optional<PersonalRecord> findByUserIdAndExerciseId(Long userId, Long exerciseId)
    {
        return personalRecordsRepository.findByUserIdAndExerciseId(userId,exerciseId);
    }

    public List<PersonalRecord> getPersonalRecordsForUser(Long userId) {

        return personalRecordsRepository.findByUserId(userId);
    }

    public Optional<PersonalRecord> findByUserHistoryExercise(UserHistoryExercise userHistoryExercise) {
        return personalRecordsRepository.findByUserHistoryExercise(userHistoryExercise);
    }
}
