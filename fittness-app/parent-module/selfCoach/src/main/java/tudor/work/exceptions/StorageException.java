package tudor.work.exceptions;

public class StorageException extends RuntimeException  {
    public StorageException(Exception exception) {
        super(exception);

    }
}
