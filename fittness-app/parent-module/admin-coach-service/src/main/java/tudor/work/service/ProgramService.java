package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.Program;
import tudor.work.repository.ProgramRepository;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;

    public Program saveProgram(Program program) {
        return programRepository.save(program);
    }

    public Program getProgramById(Long programId) throws NotFoundException {

        return programRepository.findById(programId).orElseThrow(()-> new NotFoundException("program with id "+programId.toString()+" not found"));
    }
}
