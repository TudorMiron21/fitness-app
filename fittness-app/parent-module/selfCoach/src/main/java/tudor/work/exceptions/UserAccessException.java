package tudor.work.exceptions;

public class UserAccessException extends RuntimeException{
    public UserAccessException(String msg)
    {
        super(msg);
    }
}
