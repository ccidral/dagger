package dagger.servlet3.features;

import javax.servlet.ServletContext;

public class TestFeatureB implements ServletFeature {

    private static ServletContext servletContext;

    public static boolean isEnabledWith(ServletContext servletContext) {
        return TestFeatureB.servletContext == servletContext;
    }

    @Override
    public void enable(ServletContext servletContext) {
        TestFeatureB.servletContext = servletContext;
    }

}
