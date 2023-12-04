package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.web.bind.annotation.RestController;
import tudor.work.model.UserHistoryModule;

import java.util.Optional;


@Repository
public interface UserHistoryModuleRepository extends JpaRepository<UserHistoryModule,Long> {
}
