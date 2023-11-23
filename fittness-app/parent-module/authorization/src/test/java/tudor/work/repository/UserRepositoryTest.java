package tudor.work.repository;


import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.jdbc.EmbeddedDatabaseConnection;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.test.context.junit4.SpringRunner;
import tudor.work.models.User;

import java.util.Optional;

@RunWith(SpringRunner.class)
@DataJpaTest
@AutoConfigureTestDatabase(connection = EmbeddedDatabaseConnection.H2)
public class UserRepositoryTest {

    @Autowired
    private UserRepository userRepository;


    @Test
    public void UserRepository_findByEmail_returnUser() {

        //Arrange
        String testEmail = "test.email@test.com";
        User user = User.builder().email(testEmail).build();

        userRepository.save(user);

        //Act
        Optional<User> testUser = userRepository.findByEmail(testEmail);

        //Assert
        Assertions.assertThat(testUser).isNotEmpty();
    }

    @Test
    public void UserRepository_findByResetPasswordToken_returnUser()
    {

        //Arrange
        String randTestToken = "Random Test Token";
        User user = User.builder().resetPasswordToken(randTestToken).build();

        userRepository.save(user);

        //Act
        Optional<User> testUser = userRepository.findByResetPasswordToken(randTestToken);

        //Assert
        Assertions.assertThat(testUser).isNotEmpty();
        Assertions.assertThat(testUser.get().getResetPasswordToken()).isEqualTo(user.getResetPasswordToken());
    }

}
