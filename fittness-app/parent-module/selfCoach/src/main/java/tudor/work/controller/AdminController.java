package tudor.work.controller;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping(path = "/api/selfCoach/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {
    @GetMapping("/get")
    public String get() {
        return "GET:: admin controller";
    }
    @PostMapping
    @PreAuthorize("hasAuthority(admin:write)")
    public String post() {
        return "POST:: admin controller";
    }
    @PutMapping
    @PreAuthorize("hasAuthority(admin:update)")
    public String put() {
        return "PUT:: admin controller";
    }
    @DeleteMapping
    @PreAuthorize("hasAuthority(admin:delete)")
    public String delete() {
        return "DELETE:: admin controller";
    }


}
