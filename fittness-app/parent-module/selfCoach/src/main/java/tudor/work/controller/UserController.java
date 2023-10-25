package tudor.work.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import tudor.work.dto.ExerciseDto;

import java.util.List;

@RestController
@RequestMapping(path = "/api/selfCoach/user")
public class UserController {
    @GetMapping
    public String get() {
        return "GET:: user controller";
    }
    @PostMapping
    public String post() {
        return "POST:: user controller";
    }
    @PutMapping
    public String put() {
        return "PUT:: user controller";
    }
    @DeleteMapping
    public String delete() {
        return "DELETE:: user controller";
    }


    @GetMapping("")
    public List<ExerciseDto> getAllExercises(){
        p
    }




}

