package dagger.resource;

import dagger.ResourcePattern;

public class ExactResourceName implements ResourcePattern {

    private final String resourceName;

    public ExactResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public boolean matches(String resource) {
        return this.resourceName.equals(resource);
    }

}
