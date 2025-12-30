static void process1(int age) {
    if (age <= 0) {
        throw new IllegalArgumentException();
    }
}

public class BaseException extends RuntimeException {
}
