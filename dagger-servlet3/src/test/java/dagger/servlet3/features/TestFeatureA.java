package dagger.servlet3.features;

import javax.servlet.ServletContext;

public class TestFeatureA implements ServletFeature {

    private static ServletContext servletContext;

    public static boolean isEnabledWith(ServletContext servletContext) {
        return TestFeatureA.servletContext == servletContext;
    }

    @Override
    public void enable(ServletContext servletContext) {
        TestFeatureA.servletContext = servletContext;
    }


}
