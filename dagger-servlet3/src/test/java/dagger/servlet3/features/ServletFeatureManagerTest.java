package dagger.servlet3.features;

import dagger.servlet3.TestFeatureA;
import dagger.servlet3.TestFeatureB;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class ServletFeatureManagerTest {

    private ServletContext servletContext;
    private ServletFeatureManager servletFeatureManager;

    @Before
    public void setUp() throws Exception {
        servletFeatureManager = new DefaultServletFeatureManager();
        servletContext = mock(ServletContext.class);
    }

    private void given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(String whitespaceSeparatedListOfClassNames) {
        when(servletContext.getInitParameter(ServletFeatureManager.CONFIGURATION_KEY)).thenReturn(whitespaceSeparatedListOfClassNames);
    }

    @Test
    public void test_a_servlet_feature_is_enabled_by_declaring_its_class_name_in_a_servlet_context_parameter() {
        given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(TestFeatureA.class.getName());
        servletFeatureManager.enableFeatures(servletContext);
        assertTrue("Feature is enabled", TestFeatureA.isEnabledWith(servletContext));
    }

    @Test
    public void test_more_than_one_servlet_features_may_be_enabled_by_declaring_their_class_names_separated_by_whitespace() {
        String listOfServletFeatureClassNames = String.format(
            "%s %s",
            TestFeatureA.class.getName(),
            TestFeatureB.class.getName()
        );

        given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(listOfServletFeatureClassNames);

        servletFeatureManager.enableFeatures(servletContext);

        assertTrue("Feature A is enabled", TestFeatureA.isEnabledWith(servletContext));
        assertTrue("Feature B is enabled", TestFeatureB.isEnabledWith(servletContext));
    }

    @Test
    public void test_multiple_whitespaces_are_allowed_between_servlet_feature_class_names() {
        String listOfServletFeatureClassNames = String.format(
            "%s   %s",
            TestFeatureA.class.getName(),
            TestFeatureB.class.getName()
        );

        given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(listOfServletFeatureClassNames);

        servletFeatureManager.enableFeatures(servletContext);

        assertTrue("Feature A is enabled", TestFeatureA.isEnabledWith(servletContext));
        assertTrue("Feature B is enabled", TestFeatureB.isEnabledWith(servletContext));
    }

    @Test
    public void test_trailing_and_leading_whitespaces_are_allowed_in_the_list_of_servlet_feature_class_names() {
        String listOfServletFeatureClassNames = String.format(
            "  %s  %s  ",
            TestFeatureA.class.getName(),
            TestFeatureB.class.getName()
        );

        given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(listOfServletFeatureClassNames);

        servletFeatureManager.enableFeatures(servletContext);

        assertTrue("Feature A is enabled", TestFeatureA.isEnabledWith(servletContext));
        assertTrue("Feature B is enabled", TestFeatureB.isEnabledWith(servletContext));
    }

    @Test
    public void test_tabs_and_newline_characters_are_allowed_in_the_list_of_servlet_feature_class_names() {
        String listOfServletFeatureClassNames = String.format(
            " \t \n  %s  \t  \n  %s \t\n ",
            TestFeatureA.class.getName(),
            TestFeatureB.class.getName()
        );

        given_that_a_list_of_servlet_features_is_declared_as_a_servlet_context_parameter(listOfServletFeatureClassNames);

        servletFeatureManager.enableFeatures(servletContext);

        assertTrue("Feature A is enabled", TestFeatureA.isEnabledWith(servletContext));
        assertTrue("Feature B is enabled", TestFeatureB.isEnabledWith(servletContext));
    }

}
