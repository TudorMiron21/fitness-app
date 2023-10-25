package tudor.work.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/selfCoach")
public class SelfCoachController {

    @GetMapping
    public String getAllSelfCoach(){
        return "hello there";
    }

}
