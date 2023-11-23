package tudor.work.repository;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.dto.ExerciseDto;
import tudor.work.model.User;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User,Long> {
    //Optional<User> findByUsername(String username);

    Optional<User> findByEmail(String username);
}
