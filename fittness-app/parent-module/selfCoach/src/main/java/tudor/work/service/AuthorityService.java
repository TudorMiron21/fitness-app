package tudor.work.service;


import javassist.NotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import tudor.work.model.User;
import tudor.work.repository.UserRepository;

import java.util.Collection;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthorityService {

    private final UserRepository userRepository;
    public Collection<? extends GrantedAuthority> getUserAuthorities() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            return authentication.getAuthorities();
        }

        return null;
    }

    public boolean isPayingUser() {
        List<? extends GrantedAuthority> authorities = getUserAuthorities().stream().toList();

        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_PAYING_USER"));
    }

    public boolean isUser() {
        List<? extends GrantedAuthority> authorities = getUserAuthorities().stream().toList();

        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("USER"));
    }

    public boolean isCoach() {
        List<? extends GrantedAuthority> authorities = getUserAuthorities().stream().toList();

        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_COACH"));
    }

    public boolean isAdmin() {
        List<? extends GrantedAuthority> authorities = getUserAuthorities().stream().toList();

        return authorities.stream().anyMatch(authority -> authority.getAuthority().equals("ROLE_ADMIN"));
    }

    public boolean isUserExclusive() {
        return isPayingUser() || isCoach() || isAdmin();
    }

    public String getUserName() {
        String username = "";
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Check if the user is authenticated
        if (authentication != null && authentication.isAuthenticated()) {
            username = authentication.getName();
        }

        return username;
    }

    public User getUser() throws NotFoundException{
        String username = getUserName();

        return userRepository.findByUsername(username).orElseThrow(()->new NotFoundException("User not found"));
    }


}
