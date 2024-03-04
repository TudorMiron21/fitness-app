package tudor.work.repository;


import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import tudor.work.model.CoachDetails;

import java.util.Set;

@Repository
public interface CoachDetailsRepository extends JpaRepository<CoachDetails, Long> {

    @Query("SELECT cd " +
            "FROM CoachDetails cd " +
            "WHERE cd.isValidated = false ")
    Set<CoachDetails> findAllByInvalidated();
}
