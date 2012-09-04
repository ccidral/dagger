package dagger.servlet3;

import dagger.RequestHandler;
import dagger.RequestHandlers;
import dagger.http.Request;
import org.junit.Test;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaggerServletTest {

    @Test
    public void testBootstrap() throws ServletException {
        Servlet servlet = new DaggerServlet();
        ServletConfig servletConfig = mock(ServletConfig.class);

        when(servletConfig.getInitParameter("dagger.requestHandlers.class")).thenReturn(MockRequestHandlers.class.getName());
        when(servletConfig.getInitParameter("dagger.bootstrapper.class")).thenReturn(MockBootstrapper.class.getName());

        servlet.init(servletConfig);

        assertNotNull(MockBootstrapper.requestHandlers);
        assertEquals(MockRequestHandlers.class, MockBootstrapper.requestHandlers.getClass());
    }

    public static class MockRequestHandlers implements RequestHandlers {
        public void add(RequestHandler requestHandler) {
        }

        public RequestHandler getHandlerFor(Request request) {
            return null;
        }
    }

    public static class MockBootstrapper implements Bootstrapper {
        public static RequestHandlers requestHandlers;

        public void bootstrap(RequestHandlers requestHandlers) {
            MockBootstrapper.requestHandlers = requestHandlers;
        }
    }

}
