package tudor.work.service;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.CreateProgramDto;
import tudor.work.model.Program;
import tudor.work.repository.ProgramRepository;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    public Program saveProgram(Program program) {
        return programRepository.save(program);
    }
}
