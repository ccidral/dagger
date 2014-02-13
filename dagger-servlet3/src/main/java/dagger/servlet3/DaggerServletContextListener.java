package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;
import dagger.servlet3.config.MissingConfigurationException;
import dagger.servlet3.features.ServletFeatureManager;
import dagger.servlet3.util.ObjectFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletContext;
import javax.servlet.ServletContextEvent;
import javax.servlet.ServletContextListener;

public class DaggerServletContextListener implements ServletContextListener {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void contextInitialized(ServletContextEvent servletContextEvent) {
        logger.info("Context initialized");

        ServletContext servletContext = servletContextEvent.getServletContext();
        String moduleFactoryClassName = servletContext.getInitParameter(ModuleFactory.class.getName());
        Module module = createModule(moduleFactoryClassName);
        putModuleIntoContext(module, servletContext);

        enableServletFeatures(servletContext);
    }

    private void enableServletFeatures(ServletContext servletContext) {
        String servletFeatureManagerClassName = servletContext.getInitParameter(ServletFeatureManager.class.getName());
        if(servletFeatureManagerClassName != null) {
            ServletFeatureManager servletFeatureManager = ObjectFactory.createInstanceOf(servletFeatureManagerClassName);
            servletFeatureManager.enableFeatures(servletContext);
        }
    }

    private void putModuleIntoContext(Module module, ServletContext servletContext) {
        if(module == null)
            throw new NullModuleException("The module factory produced a null module");

        servletContext.setAttribute(Module.class.getName(), module);
    }

    private Module createModule(String moduleFactoryClassName) {
        if(moduleFactoryClassName == null)
            throw new MissingConfigurationException("Missing configuration: servlet context parameter '" + ModuleFactory.class.getName() + "' is not set");

        ModuleFactory moduleFactory = ObjectFactory.createInstanceOf(moduleFactoryClassName);
        return moduleFactory.create();
    }

    @Override
    public void contextDestroyed(ServletContextEvent servletContextEvent) {
        logger.info("Context destroyed");
    }

}
