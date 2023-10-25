package tudor.work.model;

import lombok.Data;
import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static java.util.Set.of;

public enum Roles {
    USER(Set.of()),

    PAYING_USER(
            of(
                    Permissions.PAYING_USER_READ,
                    Permissions.PAYING_USER_WRITE,
                    Permissions.PAYING_USER_UPDATE,
                    Permissions.PAYING_USER_DELETE
            )
    ),

    COACH(
            of(
                    Permissions.PAYING_USER_READ,
                    Permissions.PAYING_USER_WRITE,
                    Permissions.PAYING_USER_UPDATE,
                    Permissions.PAYING_USER_DELETE,
                    Permissions.COACH_READ,
                    Permissions.COACH_WRITE,
                    Permissions.COACH_UPDATE,
                    Permissions.COACH_DELETE
            )
    ),

    ADMIN(
            of(
                    Permissions.PAYING_USER_READ,
                    Permissions.PAYING_USER_WRITE,
                    Permissions.PAYING_USER_UPDATE,
                    Permissions.PAYING_USER_DELETE,
                    Permissions.COACH_READ,
                    Permissions.COACH_WRITE,
                    Permissions.COACH_UPDATE,
                    Permissions.COACH_DELETE,
                    Permissions.ADMIN_READ,
                    Permissions.ADMIN_WRITE,
                    Permissions.ADMIN_UPDATE,
                    Permissions.ADMIN_DELETE
            )
    );

    @Getter
    private final Set<Permissions> permissions;

    public Set<Permissions> getPermissions()
    {
        return this.permissions;
    }


    Roles(Set<Permissions> permissions) {
        this.permissions = permissions;
    }


    public List<SimpleGrantedAuthority> getAuthorities() {
        var authorities = getPermissions()
                .stream()
                .map(permission -> new SimpleGrantedAuthority(permission.getPermission()))
                .collect(Collectors.toList());
        authorities.add(new SimpleGrantedAuthority("ROLE_" + this.name()));
        return authorities;
    }
}
