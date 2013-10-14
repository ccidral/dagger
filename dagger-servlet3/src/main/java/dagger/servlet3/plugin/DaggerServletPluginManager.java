package dagger.servlet3.plugin;

import dagger.Module;
import dagger.servlet3.DaggerServletException;

import javax.servlet.ServletConfig;

public interface DaggerServletPluginManager {

    void initialize(Module module, ServletConfig servletConfig) throws DaggerServletException;

    void destroy();

}
