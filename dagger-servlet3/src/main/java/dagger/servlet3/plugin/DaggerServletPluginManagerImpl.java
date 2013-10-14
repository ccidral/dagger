package dagger.servlet3.plugin;

import dagger.Module;
import dagger.servlet3.DaggerServletException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.ServletConfig;
import java.util.ArrayList;
import java.util.List;

import static dagger.servlet3.util.ObjectFactory.createInstanceOf;

public class DaggerServletPluginManagerImpl implements DaggerServletPluginManager {

    private final List<DaggerServletPlugin> plugins = new ArrayList<DaggerServletPlugin>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Override
    public void initialize(Module module, ServletConfig servletConfig) throws DaggerServletException {
        String commaSeparatedListOfPluginClassNames = servletConfig.getInitParameter("dagger-servlet-plugins");
        String[] pluginClassNames = parseListOfPluginClassNames(commaSeparatedListOfPluginClassNames);

        for(String pluginClassName : pluginClassNames) {
            DaggerServletPlugin plugin = createPlugin(pluginClassName, module, servletConfig);
            plugins.add(plugin);
        }
    }

    private String[] parseListOfPluginClassNames(String commaSeparatedListOfPluginClassNames) {
        if(commaSeparatedListOfPluginClassNames == null)
            return new String[] { };

        return commaSeparatedListOfPluginClassNames
            .trim()
            .split(" *, *");
    }

    private DaggerServletPlugin createPlugin(String pluginClassName, Module module, ServletConfig servletConfig) throws DaggerServletException {
        logger.info("Creating instance of servlet plugin: "+pluginClassName);
        DaggerServletPlugin plugin = createInstanceOf(pluginClassName);
        plugin.initialize(module, servletConfig);
        return plugin;
    }

    @Override
    public void destroy() {
        for(DaggerServletPlugin plugin : plugins)
            plugin.destroy();
    }

}
