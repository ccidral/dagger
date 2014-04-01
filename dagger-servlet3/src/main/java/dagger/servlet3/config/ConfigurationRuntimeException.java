package dagger.servlet3.config;

import dagger.DaggerRuntimeException;

public class ConfigurationRuntimeException extends DaggerRuntimeException {

    public ConfigurationRuntimeException(String message) {
        super(message);
    }

}
