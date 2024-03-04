package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;
import tudor.work.model.CoachDetails;
import tudor.work.repository.CoachDetailsRepository;

import java.util.Set;

@Service
@RequiredArgsConstructor
public class CoachDetailsService {
    private final CoachDetailsRepository coachDetailsRepository;

    public CoachDetails save(CoachDetails coachDetails) {
        return coachDetailsRepository.save(coachDetails);
    }

    public Set<CoachDetails> findAllByInvalidated() {
        return coachDetailsRepository.findAllByInvalidated();
    }

    public CoachDetails findById(Long id) throws NotFoundException {
        return coachDetailsRepository.findById(id).orElseThrow(() -> new NotFoundException("coach details entity with id " + id + " nor found"));
    }

}
