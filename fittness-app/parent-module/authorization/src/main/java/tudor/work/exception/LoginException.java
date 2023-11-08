package tudor.work.exception;

import javassist.NotFoundException;

public class LoginException extends NotFoundException {
    public LoginException(String msg, Exception e) {
        super(msg, e);
    }
}
