package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import tudor.work.model.PersonalRecord;

import java.util.List;
import java.util.Optional;

@Repository
public interface PersonalRecordsRepository extends JpaRepository<PersonalRecord, Long> {


    @Query(
            "SELECT pr " +
                    "FROM PersonalRecord pr " +
                    "JOIN pr.user u " +
                    "WHERE u.email = :userEmail"

    )
    List<PersonalRecord> findByUserEmail(@Param("userEmail") String userEmail);


    @Query(
            "SELECT pr " +
                    "FROM PersonalRecord pr " +
                    "JOIN pr.user u " +
                    "JOIN pr.exercise e " +
                    "WHERE u.id = :userId " +
                    "AND e.id = :exerciseId"
    )
    Optional<PersonalRecord> findByUserIdAndExerciseId(@Param("userId") Long userId, @Param("exerciseId") Long exerciseId);
}
