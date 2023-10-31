package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tudor.work.dto.ExerciseDto;
import tudor.work.model.Exercise;
import tudor.work.model.User;
import tudor.work.repository.ExerciseRepository;
import tudor.work.repository.UserRepository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final ExerciseService exerciseService;
    private final ExerciseRepository exerciseRepository;
    private final AuthorityService authorityService;

    public List<ExerciseDto> getAllExercises() {

        List<? extends GrantedAuthority> authorities = authorityService.getUserAuthorities().stream().toList();

        List<ExerciseDto> exercises = exerciseService.getAllUserExercises();
        if (authorityService.isPayingUser() || authorityService.isCoach() || authorityService.isAdmin()) {
            exercises.addAll(exerciseService.getAllExclusiveExercises());
        }

        return exercises;
    }


    public ExerciseDto getExerciseByName(String name) throws NotFoundException, RuntimeException {
        Exercise exercise = exerciseRepository.findByName(name).stream().findFirst().orElseThrow(() -> (new NotFoundException("exercise not found")));

        if (exercise.isExerciseExclusive() && !authorityService.isUserExclusive()) {

            throw new RuntimeException("user does not have access!");
        } else {
            return ExerciseDto
                    .builder()
                    .name(exercise.getName())
                    .description(exercise.getDescription())
                    .mediaUrl(exercise.getMediaUrl())
                    .isExerciseExclusive(exercise.isExerciseExclusive())
                    .category(exercise.getCategory())
                    .difficulty(exercise.getDifficulty())
                    .build();
        }
    }

    public User getUserByName(String name) throws NotFoundException {
        return userRepository.findByUsername(name).orElseThrow(() -> new NotFoundException("user " + name + " not found"));
    }

}
