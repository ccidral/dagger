package dagger.servlet3;

import javax.servlet.ServletException;

public class DaggerServletException extends ServletException {

    public DaggerServletException(Throwable cause) {
        super(cause);
    }

    public DaggerServletException(String message) {
        super(message);
    }

}
