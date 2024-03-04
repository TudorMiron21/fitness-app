package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import tudor.work.model.User;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User,Long> {

    Optional<User> findByEmail(String username);
}
