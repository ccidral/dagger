package dagger.resource;

import dagger.ResourceName;

public class AnyResourceName implements ResourceName {

    @Override
    public boolean matches(String uri) {
        return true;
    }

}
