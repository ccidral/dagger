package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import dagger.servlet3.plugin.DaggerServletPluginManager;
import dagger.servlet3.plugin.NullDaggerServletPluginManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

import static dagger.servlet3.util.ObjectFactory.createInstanceOf;
import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class DaggerServlet implements Servlet {

    private Module module;
    private ServletConfig servletConfig;
    private DaggerServletPluginManager pluginManager;
    private static final Logger logger = LoggerFactory.getLogger(DaggerServlet.class);

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("Initializing servlet");

        this.servletConfig = servletConfig;
        this.module = createModule(servletConfig);
        this.pluginManager = createPluginManager(servletConfig, module);
    }

    @Override
    public void destroy() {
        logger.info("Destroying servlet");
        pluginManager.destroy();
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

    private static Module createModule(ServletConfig servletConfig) throws DaggerServletException {
        String moduleFactoryClassName = getModuleFactoryClassName(servletConfig);
        logger.info("Creating instance of module factory: " + moduleFactoryClassName);
        ModuleFactory moduleFactory = createInstanceOf(moduleFactoryClassName);
        return moduleFactory.create();
    }

    private static String getModuleFactoryClassName(ServletConfig servletConfig) throws DaggerServletConfigurationException {
        String moduleFactoryClassName = servletConfig.getInitParameter("dagger.module.factory.class");
        if(moduleFactoryClassName == null)
            throw new DaggerServletConfigurationException("Module factory class is not configured");
        return moduleFactoryClassName;
    }

    private static DaggerServletPluginManager createPluginManager(ServletConfig servletConfig, Module module) throws DaggerServletException {
        String pluginManagerClass = servletConfig.getInitParameter("dagger-servlet-plugin-manager");
        if(pluginManagerClass == null)
            return new NullDaggerServletPluginManager();

        DaggerServletPluginManager pluginManager = createInstanceOf(pluginManagerClass);
        pluginManager.initialize(module, servletConfig);
        return pluginManager;
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
