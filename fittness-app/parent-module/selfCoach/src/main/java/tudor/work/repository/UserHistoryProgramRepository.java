package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tudor.work.model.UserHistoryProgram;

import java.util.Optional;

@Repository
public interface UserHistoryProgramRepository extends JpaRepository<UserHistoryProgram, Long> {

    @Query(
            "SELECT uhp FROM UserHistoryProgram uhp " +
                    "WHERE uhp.user.id = :userId " +
                    "AND uhp.program.id = :programId " +
                    "AND uhp.id = (SELECT MAX(sub.id) FROM UserHistoryProgram sub WHERE sub.user.id = :userId AND sub.program.id = :programId) "
    )
    Optional<UserHistoryProgram> findLastProgramByUserAndProgramId(@Param("userId") Long userId, @Param("programId") Long programId);
}
