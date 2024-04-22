package tudor.work.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import tudor.work.model.Program;
import tudor.work.model.User;

import java.util.Collection;
import java.util.List;

@Repository
public interface ProgramRepository extends JpaRepository<Program,Long> {

    List<Program> findAllByAdder(User coach);
}
