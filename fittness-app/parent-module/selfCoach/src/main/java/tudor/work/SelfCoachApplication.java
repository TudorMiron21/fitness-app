package tudor.work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;
import org.springframework.scheduling.annotation.EnableAsync;
import tudor.work.service.UserService;

/**
 * Hello world!
 *
 */

@EnableEurekaClient
@EnableCaching
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SelfCoachApplication
{

//    static private UserService userService;
    public static void main( String[] args )
    {
        SpringApplication.run(SelfCoachApplication.class,args);
    }
}
