package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Difficulty;
import tudor.work.repository.DifficultyRepository;

@Service
@RequiredArgsConstructor
public class DifficultyService {
    private final DifficultyRepository difficultyRepository;

    public Difficulty getDifficultyByName(String difficultyName) throws NotFoundException {
        return difficultyRepository.findByDificultyLevel(difficultyName).orElseThrow(() -> new NotFoundException("difficultyName with name " +difficultyName+ "not found"));
    }
}
