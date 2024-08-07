package tudor.work.service;

import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import net.bytebuddy.utility.RandomString;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import tudor.work.dto.AuthResponse;
import tudor.work.dto.LoginRequest;
import tudor.work.dto.RegisterRequest;
import tudor.work.exception.EmailNotFoundException;
import tudor.work.exception.RegisterException;
import tudor.work.jwt.JwtService;
import tudor.work.models.User;
import tudor.work.repository.UserRepository;

import javax.transaction.Transactional;
import java.io.UnsupportedEncodingException;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final EmailSenderService emailSenderService;


    public AuthResponse register(RegisterRequest request) throws RegisterException {

        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            var user = new User(
                    null,
                    request.getFirstName(),
                    request.getLastName(),
                    request.getEmail(),
                    passwordEncoder.encode(request.getPassword()),
                    request.getRole(),
                    null
            );

            var savedUser = userRepository.save(user);
            var jwtToken = jwtService.generateToken(user);

            return AuthResponse.builder().accessToken(jwtToken).build();
        } else {
            throw new RegisterException("username/email already present");
        }

    }

    public AuthResponse registerGoogle(RegisterRequest request) throws RegisterException {

        if (userRepository.findByEmail(request.getEmail()).isEmpty()) {
            var user = User.builder()
                    .firstname(request.getFirstName())
                    .lastname(request.getLastName())
                    .email(request.getEmail())
                    .role(request.getRole())
                    .build();

            var jwtToken = jwtService.generateToken(user);
            var savedUser = userRepository.save(user);

            return AuthResponse.builder().accessToken(jwtToken).build();
        } else {
            throw new RegisterException("username/email already present");
        }

    }

    public AuthResponse login(LoginRequest request) throws AuthenticationException, NotFoundException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        request.getEmail(),
                        request.getPassword()
                )
        );
        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("user" + request.getEmail() + " not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

    //TODO:this is not secure
    public AuthResponse loginGoogle(LoginRequest request) throws AuthenticationException, NotFoundException {

        var user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new NotFoundException("user" + request.getEmail() + " not found"));
        var jwtToken = jwtService.generateToken(user);
        return AuthResponse.builder()
                .accessToken(jwtToken)
                .build();
    }

//    public Boolean isTokenValid(String token) {
//
//        final String email = jwtService.extractUsername(token);
//
//        User user = userRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("user not found"));
//        System.out.println("user is:" + user.getUsername());
//        return jwtService.isTokenValid(token, user);
//    }


    public Boolean isTokenValid(String token) {


        return jwtService.isTokenValid(token);
    }

    public String[] getUserAuthorities(String token) {
        return jwtService.extractUserAuthorities(token);
    }

    public String getUsername(String token) {
        return jwtService.extractUsername(token);
    }


    @Transactional
    public String resetPassword(String email) throws EmailNotFoundException {
        Optional<User> user = userRepository.findByEmail(email);
        String token = RandomString.make(45);
        if (user.isPresent()) {

            user.get().setResetPasswordToken(token);
            return token;
        } else {
            throw new EmailNotFoundException("email not found");
        }
    }

    public User getUserByResetToken(String token) throws NotFoundException {
        return userRepository.findByResetPasswordToken(token).orElseThrow(() -> new NotFoundException("user cannot be found by reset password token " + token));
    }

    @Transactional
    public void updatePassword(String token, String password) throws NotFoundException {
        User user = getUserByResetToken(token);
        user.setPassword(passwordEncoder.encode(password));
        user.setResetPasswordToken(null);
    }


    public void sendResetPasswdLink(String email, String resetPasswdLink) {

        String Subject = "Here is the link to reset the password";

        String Content = "Hello, \n" +
                "You have requested to reset the password.\n" +
                "Click the link below to change your password\n"
                + resetPasswdLink + "\n" +
                "Ignore this email if you do remember your password, or you have not made the request";

        emailSenderService.sendEmail(email, Subject, Content);

    }


}
