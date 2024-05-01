package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.model.LeaderBoard;
import tudor.work.model.User;
import tudor.work.repository.LeaderBoardRepository;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LeaderBoardService {

    private final LeaderBoardRepository leaderBoardRepository;

    public Optional<LeaderBoard> findByUser(User user)
    {
        return leaderBoardRepository.findByUser(user);
    }

    public LeaderBoard save(LeaderBoard leaderBoard) {
        return leaderBoardRepository.save(leaderBoard);
    }

    public List<LeaderBoard> findAll()
    {
        return leaderBoardRepository.findAll();
    }

}
