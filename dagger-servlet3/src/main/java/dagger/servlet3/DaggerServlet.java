package dagger.servlet3;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class DaggerServlet implements Servlet {

    private Module module;
    private ServletConfig servletConfig;
    private static final Logger logger = LoggerFactory.getLogger(DaggerServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("Initializing servlet");
        this.servletConfig = servletConfig;
        this.module = getModuleFrom(servletConfig);
    }

    private Module getModuleFrom(ServletConfig servletConfig) {
        Module module = (Module) servletConfig.getServletContext().getAttribute(Module.class.getName());
        if(module == null)
            throw new NullModuleException("Module instance not found in the servlet context");
        return module;
    }

    @Override
    public void destroy() {
        logger.info("Destroying servlet");
    }

    @Override
    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    @Override
    public String getServletInfo() {
        return "Dagger Servlet";
    }

    @Override
    public void service(ServletRequest servletRequest, ServletResponse servletResponse) throws ServletException, IOException {
        Request daggerRequest = new DaggerServletRequest((HttpServletRequest) servletRequest);
        Response daggerResponse = new DaggerServletResponse((HttpServletResponse) servletResponse);
        try {
            handleRequest(daggerRequest, daggerResponse);
        } catch (Throwable e) {
            logger.error("Error while attempting to handle a request", e);
            respondWithInternalServerError(servletResponse);
        }
    }

    private void handleRequest(Request daggerRequest, Response daggerResponse) throws Exception {
        RequestHandler requestHandler = module.getHandlerFor(daggerRequest);
        Reaction reaction = requestHandler.handle(daggerRequest);
        reaction.execute(daggerRequest, daggerResponse);
    }

    private void respondWithInternalServerError(ServletResponse servletResponse) {
        ((HttpServletResponse)servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

}
