package dagger.servlet3;

public class DaggerServletConfigurationException extends DaggerServletException {

    public DaggerServletConfigurationException(Throwable cause) {
        super(cause);
    }

    public DaggerServletConfigurationException(String message) {
        super(message);
    }

}
