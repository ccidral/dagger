package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;
import dagger.servlet3.config.MissingConfigurationException;
import dagger.servlet3.features.ServletFeatureManager;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.*;

public class DaggerServletContextListenerTest {

    private ServletContextListener servletContextListener;
    private ServletContext servletContext;

    @Before
    public void setUp() throws Exception {
        servletContextListener = new DaggerServletContextListener();
        servletContext = mock(ServletContext.class);
    }

    private void given_that_module_factory_class_configuration_is_set_to(String moduleFactoryClassName) {
        when(servletContext.getInitParameter(ModuleFactory.class.getName())).thenReturn(moduleFactoryClassName);
    }

    private Module given_that_a_module_is_going_to_be_created() {
        Module module = mock(Module.class);
        MockModuleFactory.setModuleToBeCreated(module);
        given_that_module_factory_class_configuration_is_set_to(MockModuleFactory.class.getName());
        return module;
    }

    private void given_that_a_servlet_feature_manager_is_declared_as_a_servlet_context_parameter(String servletFeatureManagerClassName) {
        when(servletContext.getInitParameter(ServletFeatureManager.class.getName())).thenReturn(
            servletFeatureManagerClassName
        );
    }

    @Test(expected = MissingConfigurationException.class)
    public void test_throw_exception_when_no_module_factory_is_configured() {
        given_that_module_factory_class_configuration_is_set_to(null);
        servletContextListener.contextInitialized(new ServletContextEvent(servletContext));
    }

    @Test(expected = NullModuleException.class)
    public void test_throw_exception_when_module_factory_produces_a_null_module() {
        MockModuleFactory.setModuleToBeCreated(null);
        given_that_module_factory_class_configuration_is_set_to(MockModuleFactory.class.getName());
        servletContextListener.contextInitialized(new ServletContextEvent(servletContext));
    }

    @Test
    public void test_create_the_module_and_store_it_in_the_servlet_context() {
        Module module = given_that_a_module_is_going_to_be_created();
        servletContextListener.contextInitialized(new ServletContextEvent(servletContext));
        verify(servletContext).setAttribute(Module.class.getName(), module);
    }

    @Test
    public void test_enabled_servlet_features_when_a_servlet_feature_manager_is_declared_as_a_servlet_context_parameter() {
        given_that_a_module_is_going_to_be_created();
        given_that_a_servlet_feature_manager_is_declared_as_a_servlet_context_parameter(TestServletFeatureManager.class.getName());
        servletContextListener.contextInitialized(new ServletContextEvent(servletContext));
        assertTrue("Servlet features are enabled", TestServletFeatureManager.hasEnabledFeaturesWith(servletContext));
    }

    public static class TestServletFeatureManager implements ServletFeatureManager {

        private static ServletContext servletContext;

        @Override
        public void enableFeatures(ServletContext servletContext) {
            TestServletFeatureManager.servletContext = servletContext;
        }

        public static boolean hasEnabledFeaturesWith(ServletContext servletContext) {
            return TestServletFeatureManager.servletContext == servletContext;
        }

    }

}
