package dagger.servlet3;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.*;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.*;

public class DaggerServletTest {

    private Module module;
    private Servlet servlet;
    private ServletConfig servletConfig;
    private ServletContext servletContext;

    @Before
    public void setUp() {
        module = mock(Module.class);
        servlet = new DaggerServlet();
        servletConfig = mock(ServletConfig.class);
        servletContext = mock(ServletContext.class);
        when(servletConfig.getServletContext()).thenReturn(servletContext);
    }

    private void given_that_servlet_context_contains_module(Module module) {
        when(servletContext.getAttribute(Module.class.getName())).thenReturn(module);
    }

    private void given_that_the_servlet_is_initialized_with_module() throws ServletException {
        given_that_servlet_context_contains_module(module);
        servlet.init(servletConfig);
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

    @Test(expected = NullModuleException.class)
    public void test_throw_exception_on_attempt_to_init_without_a_module() throws Exception {
        given_that_servlet_context_contains_module(null);
        servlet.init(servletConfig);
    }

    @Test
    public void test_servlet_config_is_the_same_as_passed_into_the_init_method() throws Exception {
        given_that_the_servlet_is_initialized_with_module();
        assertSame(servletConfig, servlet.getServletConfig());
    }

    @Test
    public void test_handle_request() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);
        Reaction reaction = mock(Reaction.class);

        given_that_the_servlet_is_initialized_with_module();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, reaction);

        handleRequest();

        verify(reaction).execute(anyRequest(), any(DaggerServletResponse.class));
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_find_a_request_handler() throws Exception {
        given_that_the_servlet_is_initialized_with_module();
        given_that_an_exception_is_thrown_while_attempting_to_find_a_request_handler();

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_handle_the_request() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);

        given_that_the_servlet_is_initialized_with_module();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_an_exception_is_thrown_when_a_request_is_handled(requestHandler);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_execute_a_reaction() throws Exception {
        RequestHandler requestHandler = mock(RequestHandler.class);
        Reaction reaction = mock(Reaction.class);

        given_that_the_servlet_is_initialized_with_module();
        given_that_a_request_handler_is_found_for_the_request(requestHandler);
        given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, reaction);
        given_that_an_exception_is_thrown_when_a_reaction_is_executed(reaction);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    private DaggerServletRequest anyRequest() {
        return any(DaggerServletRequest.class);
    }

}
