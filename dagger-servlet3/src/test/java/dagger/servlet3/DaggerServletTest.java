package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DaggerServletTest {

    private Servlet servlet;
    private ServletConfig servletConfig;
    private Module module;

    @Before
    public void setUp() {
        servlet = new DaggerServlet();
        servletConfig = mock(ServletConfig.class);
        module = mock(Module.class);
        MockModuleFactory.initialize(module);
    }

    private void given_that_plugins_classes_are_defined_as_servlet_init_param(String commaSeparatedListOfPluginClasses) {
        when(servletConfig.getInitParameter("dagger-servlet-plugins")).thenReturn(commaSeparatedListOfPluginClasses);
    }

    private void given_that_the_servlet_is_initialized_with_a_module_factory() throws ServletException {
        given_that_a_module_factory_class_is_configured();
        servlet.init(servletConfig);
    }

    private void given_that_a_module_factory_class_is_configured() {
        given_that_a_module_factory_class_is_configured_as(MockModuleFactory.class.getName());
    }

    private void given_that_a_module_factory_class_is_configured_as(String className) {
        when(servletConfig.getInitParameter("dagger.module.factory.class")).thenReturn(className);
    }

    private void given_that_a_request_handler_is_found_for_the_request(RequestHandler requestHandler) {
        when(module.getHandlerFor(anyRequest())).thenReturn(requestHandler);
    }

    private void given_that_a_reaction_is_produced_when_the_request_is_handled_by(RequestHandler requestHandler, Reaction reaction) throws Exception {
        when(requestHandler.handle(anyRequest())).thenReturn(reaction);
    }

    private void given_that_an_exception_is_thrown_while_attempting_to_find_a_request_handler() {
        when(module.getHandlerFor(any(Request.class))).thenThrow(new RuntimeException());
    }

    private void given_that_an_exception_is_thrown_when_a_request_is_handled(RequestHandler requestHandler) throws Exception {
        when(requestHandler.handle(any(Request.class))).thenThrow(new RuntimeException());
    }

    private void given_that_an_exception_is_thrown_when_a_reaction_is_executed(Reaction reaction) throws Exception {
        doThrow(new RuntimeException()).when(reaction).execute(anyRequest(), any(DaggerServletResponse.class));
    }

    private HttpServletResponse handleRequest() throws ServletException, IOException {
        HttpServletResponse servletResponse = mock(HttpServletResponse.class);
        servlet.service(mock(HttpServletRequest.class), servletResponse);
        return servletResponse;
    }

    @Test
    public void test_throw_exception_when_module_factory_class_is_not_configured() {
        try {
            servlet.init(servletConfig);
            fail("Should have thrown an exception");
        } catch(ServletException servletException) {
            assertEquals("Exception class", DaggerServletConfigurationException.class, servletException.getClass());
            assertEquals("Exception description", "Module factory class is not configured", servletException.getMessage());
        }
    }

    @Test
    public void test_throw_exception_when_module_factory_class_does_not_exist() {
        given_that_a_module_factory_class_is_configured_as("foo.Bar");
        try {
            servlet.init(servletConfig);
            fail("Should have thrown an exception");
        } catch(ServletException servletException) {
            assertEquals("Exception class", DaggerServletConfigurationException.class, servletException.getClass());
            assertEquals("Is there a root cause?", true, servletException.getCause() != null);
            assertEquals("Root cause", ClassNotFoundException.class, servletException.getCause().getClass());
        }
    }

    @Test
    public void test_create_module_on_servlet_init() throws Exception {
        given_that_the_servlet_is_initialized_with_a_module_factory();
        assertEquals("Has module been created?", true, MockModuleFactory.hasModuleBeenCreated());
    }

    @Test
    public void test_servlet_config_is_the_same_as_passed_into_the_init_method() throws Exception {
        given_that_the_servlet_is_initialized_with_a_module_factory();
        assertSame(servletConfig, servlet.getServletConfig());
    }

    @Test
    public void test_handle_request() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);
        Reaction reaction = mock(Reaction.class);

        given_that_the_servlet_is_initialized_with_a_module_factory();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, reaction);

        handleRequest();

        verify(reaction).execute(anyRequest(), any(DaggerServletResponse.class));
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_find_a_request_handler() throws Exception {
        given_that_the_servlet_is_initialized_with_a_module_factory();
        given_that_an_exception_is_thrown_while_attempting_to_find_a_request_handler();

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_handle_the_request() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);

        given_that_the_servlet_is_initialized_with_a_module_factory();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_an_exception_is_thrown_when_a_request_is_handled(requestHandler);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_execute_a_reaction() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);
        Reaction reaction = mock(Reaction.class);

        given_that_the_servlet_is_initialized_with_a_module_factory();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, reaction);
        given_that_an_exception_is_thrown_when_a_reaction_is_executed(reaction);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_instantiate_plugins_on_servlet_initialization() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(FooPlugin.class.getName() + "," + BarPlugin.class.getName());
        given_that_the_servlet_is_initialized_with_a_module_factory();

        assertEquals("Has FooPlugin been correctly initialized?", true, FooPlugin.hasBeenInitializedWith(module, servletConfig));
        assertEquals("Has BarPlugin been correctly initialized?", true, BarPlugin.hasBeenInitializedWith(module, servletConfig));
    }

    @Test
    public void test_allow_whitespaces_between_plugin_class_names_in_servlet_init_param() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(
            String.format("  %s  ,  %s  ",
                FooPlugin.class.getName(),
                BarPlugin.class.getName())
        );

        given_that_the_servlet_is_initialized_with_a_module_factory();

        assertEquals("Has FooPlugin been correctly initialized?", true, FooPlugin.hasBeenInitializedWith(module, servletConfig));
        assertEquals("Has BarPlugin been correctly initialized?", true, BarPlugin.hasBeenInitializedWith(module, servletConfig));
    }

    @Test
    public void test_detroy_plugins_on_servlet_destroy() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(FooPlugin.class.getName() + "," + BarPlugin.class.getName());
        given_that_the_servlet_is_initialized_with_a_module_factory();

        servlet.destroy();

        assertEquals("Has FooPlugin been destroyed?", true, FooPlugin.hasBeenDestroyed());
        assertEquals("Has BarPlugin been destroyed?", true, BarPlugin.hasBeenDestroyed());
    }

    private DaggerServletRequest anyRequest() {
        return any(DaggerServletRequest.class);
    }

    static class MockModuleFactory implements ModuleFactory {

        private static Module module;
        private static boolean hasModuleBeenCreated;

        public static void initialize(Module module) {
            MockModuleFactory.module = module;
            hasModuleBeenCreated = false;
        }

        @Override
        public Module create() {
            hasModuleBeenCreated = true;
            return module;
        }

        public static boolean hasModuleBeenCreated() {
            return hasModuleBeenCreated;
        }

    }

    static class FooPlugin implements DaggerServletPlugin {

        private static Module module;
        private static ServletConfig servletConfig;
        private static boolean hasBeenDestroyed;

        public static boolean hasBeenInitializedWith(Module module, ServletConfig servletConfig) {
            return FooPlugin.module == module && FooPlugin.servletConfig == servletConfig;
        }

        public static boolean hasBeenDestroyed() {
            return hasBeenDestroyed;
        }

        @Override
        public void initialize(Module module, ServletConfig servletConfig) {
            FooPlugin.module = module;
            FooPlugin.servletConfig = servletConfig;
        }

        @Override
        public void destroy() {
            hasBeenDestroyed = true;
        }
    }

    static class BarPlugin implements DaggerServletPlugin {

        private static Module module;
        private static ServletConfig servletConfig;
        private static boolean hasBeenDestroyed;

        public static boolean hasBeenInitializedWith(Module module, ServletConfig servletConfig) {
            return BarPlugin.module == module && BarPlugin.servletConfig == servletConfig;
        }

        public static boolean hasBeenDestroyed() {
            return hasBeenDestroyed;
        }

        @Override
        public void initialize(Module module, ServletConfig servletConfig) {
            BarPlugin.module = module;
            BarPlugin.servletConfig = servletConfig;
        }

        @Override
        public void destroy() {
            hasBeenDestroyed = true;
        }
    }

}
