package tudor.work.service;

import com.github.dockerjava.api.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ProgramDto;
import tudor.work.model.Program;
import tudor.work.model.UserHistoryProgram;
import tudor.work.repository.ProgramRepository;

import java.util.List;
import java.util.Set;


@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    public Program findById(Long programId)
    {
        return programRepository.findById(programId).orElseThrow(() -> new NotFoundException("program with id " + programId+ " does not exist"));
    }


    public List<Program> findAllPrograms() {
        return programRepository.findAll();
    }
}
