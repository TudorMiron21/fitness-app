package tudor.work.exceptions;

import org.springframework.dao.DuplicateKeyException;

public class DuplicatesException extends DuplicateKeyException {
    public DuplicatesException(String msg) {
        super(msg);
    }
}
