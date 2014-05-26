package dagger.servlet3.config;

public class MissingConfigurationException extends ConfigurationRuntimeException {

    public MissingConfigurationException(String message) {
        super(message);
    }

}
