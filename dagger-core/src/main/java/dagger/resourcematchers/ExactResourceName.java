package dagger.resourcematchers;

import dagger.ResourceMatcher;

public class ExactResourceName implements ResourceMatcher {

    private final String resourceName;

    public ExactResourceName(String resourceName) {
        this.resourceName = resourceName;
    }

    @Override
    public boolean matches(String resourceName) {
        return this.resourceName.equals(resourceName);
    }

}
