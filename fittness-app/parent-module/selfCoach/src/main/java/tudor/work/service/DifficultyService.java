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

    public Difficulty getDifficultyByName(String name) throws NotFoundException
    {
        return difficultyRepository.findByDificultyLevel(name).orElseThrow(()->new NotFoundException("difficulty level not found"));
    }


}
