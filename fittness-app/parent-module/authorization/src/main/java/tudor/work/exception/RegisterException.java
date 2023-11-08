package tudor.work.exception;

import org.springframework.dao.DuplicateKeyException;

public class RegisterException extends DuplicateKeyException {
    public RegisterException(String msg) {
        super(msg);
    }
}
