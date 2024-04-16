package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Program;
import tudor.work.model.UserHistoryProgram;
import tudor.work.repository.UserHistoryProgramRepository;

@Service
@RequiredArgsConstructor
public class UserHistoryProgramService {

    private final UserHistoryProgramRepository userHistoryProgramRepository;


    public UserHistoryProgram save(UserHistoryProgram userHistoryProgram){
        return userHistoryProgramRepository.save(userHistoryProgram);
    }

}
