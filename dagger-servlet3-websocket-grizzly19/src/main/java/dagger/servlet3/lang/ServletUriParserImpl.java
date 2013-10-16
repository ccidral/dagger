package dagger.servlet3.lang;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ServletUriParserImpl implements ServletUriParser {
    @Override
    public ServletUri parse(String uri) {
        if(uri == null)
            throw new IllegalArgumentException("Parameter 'uri' must not be null");

        String contextPath = "";
        String resourcePath = "";

        Pattern pattern = Pattern.compile("^(/[^/]+)((/[^/]*)*)?$");
        Matcher matcher = pattern.matcher(uri);

        if(matcher.matches()) {
            contextPath = matcher.group(1);
            resourcePath = matcher.group(2);
        }

        return new ServletUriImpl(contextPath, resourcePath);
    }

    private static class ServletUriImpl implements ServletUri {

        private final String contextPath;
        private final String resourcePath;

        public ServletUriImpl(String contextPath, String resourcePath) {
            this.contextPath = contextPath;
            this.resourcePath = resourcePath;
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

}
