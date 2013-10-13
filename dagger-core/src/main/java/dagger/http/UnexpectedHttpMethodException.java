package dagger.http;

public class UnexpectedHttpMethodException extends Exception {

    public UnexpectedHttpMethodException(String method) {
        super("Unexpected HTTP method: "+method);
    }

}
