package dagger;

public class DaggerRuntimeException extends RuntimeException {

    public DaggerRuntimeException(String message) {
        super(message);
    }

    public DaggerRuntimeException(Throwable cause) {
        super(cause);
    }

}
