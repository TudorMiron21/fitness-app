package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestMapping;
import tudor.work.dto.ExerciseDto;
import tudor.work.repository.UserRepository;

import java.util.List;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;


    public List<ExerciseDto> getAllExercises()
    {
        List<ExerciseDto>
    }

    public GrantedAuthority getUserRole(){

    }


}
