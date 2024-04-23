package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.LeaderBoard;
import tudor.work.model.User;

import java.util.Optional;

@Repository
public interface LeaderBoardRepository extends JpaRepository<LeaderBoard,Long> {

    Optional<LeaderBoard> findByUser(User user);

}
