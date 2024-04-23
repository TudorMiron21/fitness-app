package tudor.work.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import javassist.NotFoundException;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;


@Setter
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Entity(name = "_user")
@Table(name = "_user")
//@EqualsAndHashCode
public class User implements UserDetails {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String firstname;

    private String lastname;

    private String email;

    private String password;

    private String resetPasswordToken;


    @Enumerated(EnumType.STRING)
    private Roles role;

    @ManyToMany(fetch = FetchType.LAZY, cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinTable(name = "User_Workouts",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_workout"))
    @JsonIgnore
    private Set<Workout> likedWorkouts;



    @ManyToMany(cascade = CascadeType.ALL)
    @JoinTable(
            name = "_user_achievements",
            joinColumns = @JoinColumn(name = "id_user"),
            inverseJoinColumns = @JoinColumn(name = "id_achievement")
    )
    @JsonManagedReference
    private Set<Achievement> achievements;


    @ManyToMany(mappedBy = "followers", cascade = CascadeType.ALL)
    @JsonManagedReference
    private Set<User> following = new HashSet<>();


    @ManyToMany
    @JoinTable(
            name = "UserRel",
            joinColumns = @JoinColumn(name = "UserId"), // user id is the id of the paying user
            inverseJoinColumns = @JoinColumn(name = "ParentId") // parent id is the id of the coach
    )
    @JsonBackReference
    private Set<User> followers = new HashSet<>();

    public void likeWorkout(Workout workout) {
        this.likedWorkouts.add(workout);
    }

    public void unlikeWorkout(Workout workout) throws NotFoundException {
        boolean removed = this.likedWorkouts.remove(workout);

        if (!removed) {
            throw new NotFoundException("workout not found");
        }
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return role.getAuthorities();
    }


    @Override
    public String getPassword() {
        return this.password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return true;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return true;
    }
}
