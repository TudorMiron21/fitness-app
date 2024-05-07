package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.dto.ProgramDto;
import tudor.work.model.Program;

import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program,Long> {

    List<Program> findByAdderId(Long userId);
}
