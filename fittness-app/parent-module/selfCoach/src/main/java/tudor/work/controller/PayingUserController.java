package tudor.work.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


@RestController
@RequestMapping("/api/selfCoach/payingUser")
@PreAuthorize("not hasRole('USER')")
public class PayingUserController {
    @GetMapping
    public String get() {
        return "GET:: paying user controller";
    }
    @PostMapping
    public String post() {
        return "POST:: paying user controller";
    }
    @PutMapping
    public String put() {
        return "PUT:: paying user controller";
    }
    @DeleteMapping
    public String delete() {
        return "DELETE:: paying user controller";
    }
}

