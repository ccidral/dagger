package dagger.servlet3.features;

import javax.servlet.ServletContext;

public interface ServletFeature {

    void enable(ServletContext servletContext);

}
