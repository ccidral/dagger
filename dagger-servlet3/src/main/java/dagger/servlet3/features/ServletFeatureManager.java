package dagger.servlet3.features;

import javax.servlet.ServletContext;

public interface ServletFeatureManager {

    String CONFIGURATION_KEY = "dagger.servlet3.features";

    void enableFeatures(ServletContext servletContext);

}
