package tudor.work.service;


import io.minio.errors.*;
import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.dto.ProgramDto;
import tudor.work.model.Program;
import tudor.work.repository.ProgramRepository;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ProgramService {

    private final ProgramRepository programRepository;
    private final MinioService minioService;

    public Program saveProgram(Program program) {
        return programRepository.save(program);
    }

    public Program getProgramById(Long programId) throws NotFoundException {

        return programRepository.findById(programId).orElseThrow(() -> new NotFoundException("program with id " + programId.toString() + " not found"));
    }

    private boolean needsConversion(String url) {
        if (url != null)
            return !(url.startsWith("http://") || url.startsWith("https://"));
        return false;
    }

    private Program convertProgramCoverPhotos(Program program) {
        String programCoverPhotoUrl = program.getCoverPhotoUrl();
        if (needsConversion(programCoverPhotoUrl)) {
            try {
                program.setCoverPhotoUrl(minioService.generatePreSignedUrl(programCoverPhotoUrl));
            } catch (ServerException | InsufficientDataException | ErrorResponseException | IOException |
                     NoSuchAlgorithmException | InvalidKeyException | InvalidResponseException | XmlParserException |
                     InternalException e) {
                throw new RuntimeException(e);
            }
        }
        return program;
    }

    public List<Program> getAllByAdderId(Long userId) {
        return programRepository.findByAdderId(userId).stream().map(this::convertProgramCoverPhotos).toList();
    }

    public Program findById(Long programId) throws NotFoundException {
        return programRepository.findById(programId).orElseThrow(() ->new NotFoundException("program with id "+programId+ " npt found"));
    }

    public void delete(Program program){
        programRepository.delete(program);
    }
}
