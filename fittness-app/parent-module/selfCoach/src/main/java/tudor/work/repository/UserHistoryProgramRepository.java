package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.UserHistoryProgram;

@Repository
public interface UserHistoryProgramRepository extends JpaRepository<UserHistoryProgram,Long> {
}
