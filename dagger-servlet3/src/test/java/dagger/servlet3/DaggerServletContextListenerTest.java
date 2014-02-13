package dagger.servlet3;

import dagger.servlet3.config.MissingConfigurationException;
import dagger.Module;
import dagger.ModuleFactory;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

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
        Module module = mock(Module.class);
        MockModuleFactory.setModuleToBeCreated(module);

        given_that_module_factory_class_configuration_is_set_to(MockModuleFactory.class.getName());

        servletContextListener.contextInitialized(new ServletContextEvent(servletContext));

        verify(servletContext).setAttribute(Module.class.getName(), module);
    }

}
