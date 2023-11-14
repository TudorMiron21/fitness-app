package tudor.work.exceptions;


import javax.naming.AuthenticationException;

public class AuthorizationExceptionHandler extends AuthenticationException {

    public AuthorizationExceptionHandler(String message)
    {
        super(message);
    }

}
