package system.task_management.exception;

public class UserEntityNotFound extends RuntimeException {
    public UserEntityNotFound(String message) {
        super(message);
    }
}
