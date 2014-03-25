package dagger.servlet3;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import dagger.lang.Converter;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;

public class DaggerServletTest {

    private Servlet servlet;

    @Mock private Module module;
    @Mock private ServletConfig servletConfig;
    @Mock private ServletContext servletContext;
    @Mock private HttpServletRequest httpServletRequest;
    @Mock private HttpServletResponse httpServletResponse;
    @Mock private Converter<HttpServletRequest, Request> requestConverter;
    @Mock private Converter<HttpServletResponse, Response> responseConverter;

    @Before
    public void setUp() {
        initMocks(this);

        servlet = new DaggerServlet(requestConverter, responseConverter);

        when(servletConfig.getServletContext()).thenReturn(servletContext);
    }

    private void given_that_servlet_context_contains_module(Module module) {
        when(servletContext.getAttribute(Module.class.getName())).thenReturn(module);
    }

    private void given_that_the_servlet_is_initialized_with_module() throws ServletException {
        given_that_servlet_context_contains_module(module);
        servlet.init(servletConfig);
    }

    private RequestHandler given_that_a_request_handler_is_found_for_the_request(Request request) {
        RequestHandler requestHandler = mock(RequestHandler.class);
        when(module.getHandlerFor(request)).thenReturn(requestHandler);
        return requestHandler;
    }

    private Reaction given_that_a_reaction_is_produced_when_the_request_is_handled_by(RequestHandler requestHandler, Request request) throws Exception {
        Reaction reaction = mock(Reaction.class);
        when(requestHandler.handle(request)).thenReturn(reaction);
        return reaction;
    }

    private void given_that_an_exception_is_thrown_while_attempting_to_find_a_request_handler_for(Request request) {
        when(module.getHandlerFor(request)).thenThrow(new RuntimeException());
    }

    private void given_that_an_exception_is_thrown_when_request_is_handled_by(RequestHandler requestHandler, Request request) throws Exception {
        when(requestHandler.handle(request)).thenThrow(new RuntimeException());
    }

    private void given_that_an_exception_is_thrown_when_reaction_is_executed(Reaction reaction) throws Exception {
        doThrow(new RuntimeException()).when(reaction).execute(any(Request.class), any(Response.class));
    }

    private HttpServletResponse handleRequest() throws ServletException, IOException {
        servlet.service(httpServletRequest, httpServletResponse);
        return httpServletResponse;
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
        given_that_the_servlet_is_initialized_with_module();

        Request request = given_that_the_servlet_request_is_converted_to_a_dagger_request();
        Response response = given_that_the_servlet_response_is_converted_to_a_dagger_response();
        RequestHandler requestHandler = given_that_a_request_handler_is_found_for_the_request(request);
        Reaction reaction = given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, request);

        handleRequest();

        verify(reaction).execute(request, response);
    }

    private Response given_that_the_servlet_response_is_converted_to_a_dagger_response() {
        Response response = mock(Response.class);
        when(responseConverter.convert(httpServletResponse)).thenReturn(response);
        return response;
    }

    private Request given_that_the_servlet_request_is_converted_to_a_dagger_request() {
        Request request = mock(Request.class);
        when(requestConverter.convert(httpServletRequest)).thenReturn(request);
        return request;
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_find_a_request_handler() throws Exception {
        given_that_the_servlet_is_initialized_with_module();

        Request request = given_that_the_servlet_request_is_converted_to_a_dagger_request();
        given_that_an_exception_is_thrown_while_attempting_to_find_a_request_handler_for(request);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_handle_the_request() throws Exception {
        given_that_the_servlet_is_initialized_with_module();

        Request request = given_that_the_servlet_request_is_converted_to_a_dagger_request();
        RequestHandler requestHandler = given_that_a_request_handler_is_found_for_the_request(request);
        given_that_an_exception_is_thrown_when_request_is_handled_by(requestHandler, request);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    @Test
    public void test_respond_with_status_code_500_on_error_during_attempt_to_execute_a_reaction() throws Exception {
        given_that_the_servlet_is_initialized_with_module();

        Request request = given_that_the_servlet_request_is_converted_to_a_dagger_request();
        RequestHandler requestHandler = given_that_a_request_handler_is_found_for_the_request(request);
        Reaction reaction = given_that_a_reaction_is_produced_when_the_request_is_handled_by(requestHandler, request);
        given_that_an_exception_is_thrown_when_reaction_is_executed(reaction);

        HttpServletResponse servletResponse = handleRequest();

        verify(servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

}
