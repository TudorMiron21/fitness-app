package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.UserHistoryExercise;
import tudor.work.model.UserHistoryModule;
import tudor.work.repository.UserHistoryModuleRepository;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserHistoryModuleService {

    private final UserHistoryModuleRepository userHistoryModuleRepository;

    public UserHistoryModule getModuleById(Long id) throws NotFoundException {
        return userHistoryModuleRepository
                .findById(id).orElseThrow(()-> new NotFoundException("module with id " + id + " not found"));
    }
}
