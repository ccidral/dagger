package dagger.servlet3;

import dagger.Module;
import dagger.servlet3.plugin.DaggerServletPlugin;
import dagger.servlet3.plugin.DaggerServletPluginManager;
import dagger.servlet3.plugin.DaggerServletPluginManagerImpl;
import org.junit.Before;
import org.junit.Test;

import javax.servlet.ServletConfig;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class DaggerServletPluginManagerTest {

    private DaggerServletPluginManager pluginManager;
    private ServletConfig servletConfig;
    private Module module;

    private void given_that_plugins_classes_are_defined_as_servlet_init_param(String commaSeparatedListOfPluginClasses) {
        when(servletConfig.getInitParameter("dagger-servlet-plugins")).thenReturn(commaSeparatedListOfPluginClasses);
    }

    @Before
    public void setUp() throws Exception {
        pluginManager = new DaggerServletPluginManagerImpl();
        servletConfig = mock(ServletConfig.class);
        module = mock(Module.class);
    }

    @Test
    public void test_instantiate_plugins_on_servlet_initialization() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(FooPlugin.class.getName() + "," + BarPlugin.class.getName());

        pluginManager.initialize(module, servletConfig);

        assertEquals("Has FooPlugin been correctly initialized?", true, FooPlugin.hasBeenInitializedWith(module, servletConfig));
        assertEquals("Has BarPlugin been correctly initialized?", true, BarPlugin.hasBeenInitializedWith(module, servletConfig));
    }

    @Test
    public void test_allow_whitespaces_between_plugin_class_names_in_servlet_init_param() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(
            String.format("  %s  ,  %s  ",
                FooPlugin.class.getName(),
                BarPlugin.class.getName())
        );

        pluginManager.initialize(module, servletConfig);

        assertEquals("Has FooPlugin been correctly initialized?", true, FooPlugin.hasBeenInitializedWith(module, servletConfig));
        assertEquals("Has BarPlugin been correctly initialized?", true, BarPlugin.hasBeenInitializedWith(module, servletConfig));
    }

    @Test
    public void test_detroy_plugins_on_servlet_destroy() throws Throwable {
        given_that_plugins_classes_are_defined_as_servlet_init_param(FooPlugin.class.getName() + "," + BarPlugin.class.getName());

        pluginManager.initialize(module, servletConfig);
        pluginManager.destroy();

        assertEquals("Has FooPlugin been destroyed?", true, FooPlugin.hasBeenDestroyed());
        assertEquals("Has BarPlugin been destroyed?", true, BarPlugin.hasBeenDestroyed());
    }


    public static class FooPlugin implements DaggerServletPlugin {

        private static Module module;
        private static ServletConfig servletConfig;
        private static boolean hasBeenDestroyed;

        public static boolean hasBeenInitializedWith(Module module, ServletConfig servletConfig) {
            return FooPlugin.module == module && FooPlugin.servletConfig == servletConfig;
        }

        public static boolean hasBeenDestroyed() {
            return hasBeenDestroyed;
        }

        @Override
        public void initialize(Module module, ServletConfig servletConfig) {
            FooPlugin.module = module;
            FooPlugin.servletConfig = servletConfig;
        }

        @Override
        public void destroy() {
            hasBeenDestroyed = true;
        }
    }

    public static class BarPlugin implements DaggerServletPlugin {

        private static Module module;
        private static ServletConfig servletConfig;
        private static boolean hasBeenDestroyed;

        public static boolean hasBeenInitializedWith(Module module, ServletConfig servletConfig) {
            return BarPlugin.module == module && BarPlugin.servletConfig == servletConfig;
        }

        public static boolean hasBeenDestroyed() {
            return hasBeenDestroyed;
        }

        @Override
        public void initialize(Module module, ServletConfig servletConfig) {
            BarPlugin.module = module;
            BarPlugin.servletConfig = servletConfig;
        }

        @Override
        public void destroy() {
            hasBeenDestroyed = true;
        }
    }

}
