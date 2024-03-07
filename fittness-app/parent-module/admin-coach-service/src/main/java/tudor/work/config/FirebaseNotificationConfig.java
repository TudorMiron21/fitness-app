package tudor.work.config;

import com.google.auth.oauth2.GoogleCredentials;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.messaging.FirebaseMessaging;
import org.checkerframework.common.value.qual.BottomVal;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import java.io.IOException;

@Configuration
public class FirebaseNotificationConfig {

    @Value("${app.firebase-configuration-file}")
    private String adminSdkPrivateKeyPath;

    @Value("${app.firebase-app-name")
    private String firebaseAppName;
    @Bean
    FirebaseMessaging firebaseMessaging() throws IOException {
        GoogleCredentials googleCredentials = GoogleCredentials.fromStream(
                new ClassPathResource(adminSdkPrivateKeyPath).getInputStream()
        );

        FirebaseOptions firebaseOptions = FirebaseOptions
                .builder()
                .setCredentials(googleCredentials)
                .build();

        FirebaseApp firebaseApp = FirebaseApp.initializeApp(firebaseOptions,firebaseAppName);

        return FirebaseMessaging.getInstance(firebaseApp);

    }

}
