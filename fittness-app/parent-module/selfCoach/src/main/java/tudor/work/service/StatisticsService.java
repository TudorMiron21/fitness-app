package tudor.work.service;

import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import tudor.work.repository.StatisticsRepository;

import javax.validation.constraints.AssertFalse;


@RequiredArgsConstructor
@Service
public class StatisticsService {

    private final StatisticsRepository statisticsRepository;
    private final  AuthorityService authorityService;
    private String userEmail;

    public void StatisticsRepository(){
        userEmail = authorityService.getEmail();
    }
    @Async
    public Integer totalCaloriesBurned(){

    }

    public String totalTimeDuringExercises(){}



}
