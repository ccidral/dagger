package dagger.servlet3.plugin;

import dagger.Module;
import dagger.servlet3.DaggerServletConfigurationException;

import javax.servlet.ServletConfig;

public class NullDaggerServletPluginManager implements DaggerServletPluginManager {

    @Override
    public void initialize(Module module, ServletConfig servletConfig) throws DaggerServletConfigurationException {
    }

    @Override
    public void destroy() {
    }

}
