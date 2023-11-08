package tudor.work.exception;

import javassist.NotFoundException;

public class EmailNotFoundException extends NotFoundException {
    public EmailNotFoundException(String msg) {
        super(msg);
    }
}
