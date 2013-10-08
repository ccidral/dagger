package dagger.servlet3;

import dagger.Module;

import javax.servlet.ServletConfig;

public interface DaggerServletPlugin {

    void initialize(Module module, ServletConfig servletConfig);

    void destroy();

}
