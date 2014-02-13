package dagger.servlet3.features;

import dagger.servlet3.ServletFeature;
import dagger.servlet3.util.ObjectFactory;

import javax.servlet.ServletContext;

public class DefaultServletFeatureManager implements ServletFeatureManager {

    @Override
    public void enableFeatures(ServletContext servletContext) {
        String whitespaceSeparatedClassNames = servletContext.getInitParameter(CONFIGURATION_KEY);
        if(whitespaceSeparatedClassNames != null) {
            String[] classNames = whitespaceSeparatedClassNames.split("\\s+");
            enableFeatures(classNames);
        }
    }

    private void enableFeatures(String[] classNames) {
        for(String className : classNames)
            if(className.length() > 0)
                enableFeature(className);
    }

    private void enableFeature(String className) {
        ServletFeature feature = ObjectFactory.createInstanceOf(className);
        feature.enable();
    }

}
