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
import java.util.ArrayList;
import java.util.List;

import static javax.servlet.http.HttpServletResponse.SC_INTERNAL_SERVER_ERROR;

public class DaggerServlet implements Servlet {

    private Module module;
    private ServletConfig servletConfig;
    private final Logger logger = LoggerFactory.getLogger(getClass());
    private List<DaggerServletPlugin> plugins = new ArrayList<>();

    @Override
    public void init(ServletConfig servletConfig) throws ServletException {
        logger.info("Initializing servlet");

        this.servletConfig = servletConfig;
        this.module = createModule(servletConfig);
        this.plugins = createPlugins();
    }

    @Override
    public void destroy() {
        logger.info("Destroying servlet");
        destroyPlugins();
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

    private Module createModule(ServletConfig servletConfig) throws DaggerServletConfigurationException {
        String moduleFactoryClassName = getModuleFactoryClassName(servletConfig);
        logger.info("Creating instance of module factory: " + moduleFactoryClassName);
        ModuleFactory moduleFactory = createInstanceOf(moduleFactoryClassName);
        return moduleFactory.create();
    }

    private String getModuleFactoryClassName(ServletConfig servletConfig) throws DaggerServletConfigurationException {
        String moduleFactoryClassName = servletConfig.getInitParameter("dagger.module.factory.class");
        if(moduleFactoryClassName == null)
            throw new DaggerServletConfigurationException("Module factory class is not configured");
        return moduleFactoryClassName;
    }

    private List<DaggerServletPlugin> createPlugins() throws DaggerServletConfigurationException {
        List<DaggerServletPlugin> createdPlugins = new ArrayList<>();
        String commaSeparatedListOfPluginClassNames = servletConfig.getInitParameter("dagger-servlet-plugins");
        String[] pluginClassNames = parseListOfPluginClassNames(commaSeparatedListOfPluginClassNames);

        for(String pluginClassName : pluginClassNames) {
            DaggerServletPlugin plugin = createPlugin(pluginClassName);
            createdPlugins.add(plugin);
        }

        return createdPlugins;
    }

    private DaggerServletPlugin createPlugin(String pluginClassName) throws DaggerServletConfigurationException {
        logger.info("Creating instance of servlet plugin: "+pluginClassName);
        DaggerServletPlugin plugin = createInstanceOf(pluginClassName);
        plugin.initialize(module, servletConfig);
        return plugin;
    }

    private void destroyPlugins() {
        for(DaggerServletPlugin plugin : plugins)
            plugin.destroy();
    }

    private String[] parseListOfPluginClassNames(String commaSeparatedListOfPluginClassNames) {
        if(commaSeparatedListOfPluginClassNames == null)
            return new String[] { };

        return commaSeparatedListOfPluginClassNames
            .trim()
            .split(" *, *");
    }

    private void handleRequest(Request daggerRequest, Response daggerResponse) throws Exception {
        RequestHandler requestHandler = module.getHandlerFor(daggerRequest);
        Reaction reaction = requestHandler.handle(daggerRequest);
        reaction.execute(daggerRequest, daggerResponse);
    }

    private void respondWithInternalServerError(ServletResponse servletResponse) {
        ((HttpServletResponse)servletResponse).setStatus(SC_INTERNAL_SERVER_ERROR);
    }

    private <T> T createInstanceOf(String className) throws DaggerServletConfigurationException {
        Class<?> clazz;
        try {
            clazz = Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new DaggerServletConfigurationException(e);
        }
        try {
            return (T) clazz.newInstance();
        } catch (InstantiationException e) {
            throw new DaggerServletConfigurationException(e);
        } catch (IllegalAccessException e) {
            throw new DaggerServletConfigurationException(e);
        }
    }

}
