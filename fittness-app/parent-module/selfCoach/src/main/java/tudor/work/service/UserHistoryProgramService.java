package tudor.work.service;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Program;
import tudor.work.model.UserHistoryProgram;
import tudor.work.repository.UserHistoryProgramRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHistoryProgramService {

    private final UserHistoryProgramRepository userHistoryProgramRepository;

    public UserHistoryProgram findById(Long id)
    {
        return userHistoryProgramRepository.findById(id).orElseThrow(() -> new NotFoundException("user history program with id "+id+" not found"));
    }

    public UserHistoryProgram save(UserHistoryProgram userHistoryProgram){
        return userHistoryProgramRepository.save(userHistoryProgram);
    }

    public Optional<UserHistoryProgram> findLastProgramByUserAndProgramId(Long userId, Long programId) {
        return userHistoryProgramRepository.findLastProgramByUserAndProgramId(userId,programId);
    }
}
