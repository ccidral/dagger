package dagger.servlet3;

import dagger.DaggerRuntimeException;

public class NullModuleException extends DaggerRuntimeException {

    public NullModuleException(String message) {
        super(message);
    }

}
