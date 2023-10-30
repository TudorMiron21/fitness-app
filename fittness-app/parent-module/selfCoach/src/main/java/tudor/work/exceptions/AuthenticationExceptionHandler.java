package tudor.work.exceptions;


import javax.naming.AuthenticationException;

public class AuthenticationExceptionHandler extends AuthenticationException {

    public AuthenticationExceptionHandler(String message)
    {
        super(message);
    }

}
