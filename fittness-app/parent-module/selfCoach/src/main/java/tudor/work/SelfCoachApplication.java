package tudor.work;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.netflix.eureka.EnableEurekaClient;

/**
 * Hello world!
 *
 */

@EnableEurekaClient
@SpringBootApplication(exclude = { SecurityAutoConfiguration.class })
public class SelfCoachApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(SelfCoachApplication.class,args);
    }
}
