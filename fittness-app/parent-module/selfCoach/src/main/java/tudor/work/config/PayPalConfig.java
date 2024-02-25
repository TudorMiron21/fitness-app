package tudor.work.config;


import lombok.Getter;
import lombok.Setter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;


@Getter
@Setter
@Configuration
@ConfigurationProperties(prefix = "paypal")
public class PayPalConfig
{
    @Value("${paypal.client.id}")
    private String clientId;

    @Value("${paypal.client.secret}")
    private String clientSecret;

    @Value("${paypal.client.baseUrl}")
    private String baseUrl;

    @Value("${paypal.mode}")
    private String mode;

//    @Bean
//    public PayPalHttpClient getPaypalClient(
//            @Value("${paypal.clientId}") String clientId,
//            @Value("${paypal.clientSecret}") String clientSecret) {
//        return new PayPalHttpClient(new PayPalEnvironment.Sandbox(clientId, clientSecret));
//    }
}
