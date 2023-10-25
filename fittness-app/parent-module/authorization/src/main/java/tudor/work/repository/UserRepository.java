package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tudor.work.models.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {
    Optional<User> findByUsername(String username);
}
