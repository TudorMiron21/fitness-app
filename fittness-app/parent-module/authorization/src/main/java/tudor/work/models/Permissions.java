package tudor.work.models;

import lombok.Getter;

@Getter
public enum Permissions {
    PAYING_USER_READ("payingUser:read"),
    PAYING_USER_WRITE("payingUser:write"),
    PAYING_USER_UPDATE("payingUser:update"),
    PAYING_USER_DELETE("payingUser:delete"),
    COACH_READ("coach:read"),
    COACH_WRITE("coach:write"),
    COACH_UPDATE("coach:update"),
    COACH_DELETE("coach:delete"),
    ADMIN_READ("admin:read"),
    ADMIN_WRITE("admin:write"),
    ADMIN_UPDATE("admin:update"),
    ADMIN_DELETE("admin:delete");

    private final String permission;

    Permissions(String permission) {
        this.permission = permission;
    }

    public String getPermission()
    {
        return this.permission;
    }
}
