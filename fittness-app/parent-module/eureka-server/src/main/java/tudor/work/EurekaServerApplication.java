package tudor.work;

import com.sun.xml.bind.v2.TODO;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.netflix.eureka.server.EnableEurekaServer;


@SpringBootApplication
@EnableEurekaServer
public class EurekaServerApplication
{
    public static void main( String[] args )
    {
        SpringApplication.run(EurekaServerApplication.class,args);
    }
}
