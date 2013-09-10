package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;
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
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("Initializing servlet");
        this.servletConfig = servletConfig;
        this.module = createModule(servletConfig);
    }

    private Module createModule(ServletConfig servletConfig) throws DaggerServletConfigurationException {
        String moduleFactoryClassName = getModuleFactoryClassName(servletConfig);
        ModuleFactory moduleFactory = createModuleFactory(moduleFactoryClassName);
        return moduleFactory.create();
    }

    private String getModuleFactoryClassName(ServletConfig servletConfig) throws DaggerServletConfigurationException {
        String moduleFactoryClassName = servletConfig.getInitParameter("dagger.module.factory.class");
        if(moduleFactoryClassName == null)
            throw new DaggerServletConfigurationException("Module factory class is not configured");
        return moduleFactoryClassName;
    }

    public ServletConfig getServletConfig() {
        return servletConfig;
    }

    public String getServletInfo() {
        return "Dagger Servlet";
    }

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

    public void destroy() {
    }

    private ModuleFactory createModuleFactory(String className) throws DaggerServletConfigurationException {
        logger.info("Creating instance of module factory: " + className);
        Class<ModuleFactory> clazz;
        try {
            clazz = (Class<ModuleFactory>) Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DaggerServletConfigurationException(e);
        }
        try {
            return clazz.newInstance();
        } catch (InstantiationException e) {
            throw new DaggerServletConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new DaggerServletConfigurationException(e);
        }
    }

}
