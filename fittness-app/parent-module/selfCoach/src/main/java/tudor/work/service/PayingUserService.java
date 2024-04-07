package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import tudor.work.exceptions.DuplicateCoachSubscription;

import tudor.work.model.User;

import javax.transaction.Transactional;
import java.time.LocalDateTime;
import java.util.Objects;
import java.util.Set;

@Service
@RequiredArgsConstructor
public class PayingUserService {

    private final AuthorityService authorityService;
    private final UserService userService;

    @Transactional
    public void subscribeToCoach(Long coachId) throws NotFoundException, DuplicateCoachSubscription {

        User payingUser = userService.findById(authorityService.getUserId());
        User coach = userService.findById(coachId);

        if (!payingUser.getFollowing().add(coach))
            throw new DuplicateCoachSubscription("paying user" +payingUser.getId()+" already subscribed to coach" + coachId);

        if(!coach.getFollowers().add(payingUser))
            throw new DuplicateCoachSubscription("coach" +coachId+ " has paying user" +payingUser.getId() +"as a subscriber");


    }

}
