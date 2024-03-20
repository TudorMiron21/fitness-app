package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Program;

@Repository
public interface ProgramRepository extends JpaRepository<Program,Long> {

}
