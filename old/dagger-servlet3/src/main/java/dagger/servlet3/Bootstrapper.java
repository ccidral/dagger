package dagger.servlet3;

import dagger.RequestHandlers;

public interface Bootstrapper {

    void bootstrap(RequestHandlers requestHandlers);

}
