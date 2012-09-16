package dagger.resource;

import dagger.ResourceName;

public class ExactResourceName implements ResourceName {

    private final String resourceName;

    public ExactResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public boolean matches(String uri) {
        return this.resourceName.equals(uri);
    }

}
