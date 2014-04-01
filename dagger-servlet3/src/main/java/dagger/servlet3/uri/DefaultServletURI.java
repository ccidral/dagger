package dagger.servlet3.uri;

public class DefaultServletURI implements ServletURI {

    private final String contextPath;
    private final String resourcePath;

    public DefaultServletURI(String uri) {
        int resourcePathPosition = uri.indexOf('/', 1);
        contextPath = resourcePathPosition > -1 ? uri.substring(0, resourcePathPosition) : uri;
        resourcePath = resourcePathPosition > -1 ? uri.substring(resourcePathPosition) : null;
    }

    @Override
    public String getContextPath() {
        return contextPath;
    }

    @Override
    public String getResourcePath() {
        return resourcePath;
    }

}
