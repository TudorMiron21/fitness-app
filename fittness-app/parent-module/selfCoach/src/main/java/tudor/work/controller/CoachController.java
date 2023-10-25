package tudor.work.controller;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

@RestController
@RequestMapping(path = "/api/selfCoach/coach")
@PreAuthorize("hasRole('COACH') or hasRole('ADMIN')")
public class CoachController {
    @GetMapping
    @PreAuthorize("hasAuthority(admin:read) or hasAuthority(coach:read)")
    public String get() {
        return "GET:: coach controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority(admin:write) or hasAuthority(coach:write)")
    public String post() {
        return "POST:: coach controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority(admin:update) or hasAuthority(coach:update)")
    public String put() {
        return "PUT:: coach controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority(admin:delete) or hasAuthority(coach:delete)")
    public String delete() {
        return "DELETE:: coach controller";
    }
}

