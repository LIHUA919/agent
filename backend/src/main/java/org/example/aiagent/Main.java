static void process1(int age) {
    if (age <= 0) {
        throw new IllegalArgumentException();
    }
}

public class BaseException extends RuntimeException {
}

public class UserNotFoundException extends BaseException {
}

public class LoginFailedException extends BaseException {
}

public class BaseException extends RuntimeException {
    public BaseException() {
        super();
    }

    public BaseException(String message, Throwable cause) {
        super(message, cause);
    }

    public BaseException(String message) {
        super(message);
    }


