package tudor.work.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping(path = "/api/v1/adminCoachService/admin")
public class AdminController {

    @GetMapping("/hello")
    public String getAdminHelloMessage()
    {
        return "hello admin";
    }
}
