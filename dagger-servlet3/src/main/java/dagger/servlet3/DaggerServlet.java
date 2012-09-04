package dagger.servlet3;

import dagger.RequestHandlers;

import javax.servlet.*;
import java.io.IOException;

public class DaggerServlet implements Servlet {
    public void init(ServletConfig servletConfig) throws ServletException {
        String requestHandlersClassName = servletConfig.getInitParameter("dagger.requestHandlers.class");
        String bootstrapperClassName = servletConfig.getInitParameter("dagger.bootstrapper.class");

        RequestHandlers requestHandlers = newInstanceOf(requestHandlersClassName);
        Bootstrapper bootstrapper = newInstanceOf(bootstrapperClassName);

        bootstrapper.bootstrap(requestHandlers);
    }

    private <T> T newInstanceOf(String className) {
        Class<T> clazz = null;
        try {
            clazz = (Class<T>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    public ServletConfig getServletConfig() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        //To change body of implemented methods use File | Settings | File Templates.
    }

    public String getServletInfo() {
        return null;  //To change body of implemented methods use File | Settings | File Templates.
    }

    public void destroy() {
        //To change body of implemented methods use File | Settings | File Templates.
    }
}
