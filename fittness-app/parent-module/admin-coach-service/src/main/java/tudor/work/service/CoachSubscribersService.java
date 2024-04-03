package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.repository.CoachSubscribersRepository;

@Service
@RequiredArgsConstructor
public class CoachSubscribersService {

    private final CoachSubscribersRepository coachSubscribersRepository;

    
}
