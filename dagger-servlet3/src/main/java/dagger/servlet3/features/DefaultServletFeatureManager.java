package dagger.servlet3.features;

import dagger.servlet3.util.ObjectFactory;

import javax.servlet.ServletContext;

public class DefaultServletFeatureManager implements ServletFeatureManager {

    @Override
    public void enableFeatures(ServletContext servletContext) {
        String whitespaceSeparatedClassNames = servletContext.getInitParameter(CONFIGURATION_KEY);
        if(whitespaceSeparatedClassNames != null) {
            String[] classNames = whitespaceSeparatedClassNames.split("\\s+");
            enableFeatures(classNames, servletContext);
        }
    }

    private void enableFeatures(String[] classNames, ServletContext servletContext) {
        for(String className : classNames)
            if(className.length() > 0)
                enableFeature(className, servletContext);
    }

    private void enableFeature(String className, ServletContext servletContext) {
        ServletFeature feature = ObjectFactory.createInstanceOf(className);
        feature.enable(servletContext);
    }

}
