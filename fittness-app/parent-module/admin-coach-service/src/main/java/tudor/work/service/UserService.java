package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.User;
import tudor.work.repository.UserRepository;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    public User getUserByEmail(String email) throws NotFoundException {
        return userRepository.findByEmail(email).orElseThrow(() -> new NotFoundException("user " + email + " not found"));
    }
}
