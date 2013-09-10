package dagger.server.netty;

public class WebServer {

    private final Class<?> nettyServerClass;
    private final Class<?> moduleFactoryClass;
    private final Class<?> moduleInterface;
    private Object nettyServer;

    public WebServer(String moduleFactoryClassName, ClassLoader classLoader) throws Exception {
        this.moduleFactoryClass = classLoader.loadClass(moduleFactoryClassName);
        this.moduleInterface = classLoader.loadClass("dagger.Module");
        this.nettyServerClass = classLoader.loadClass("dagger.server.netty.NettyServer");
    }

    public void start() throws Exception {
        Object module = createModule();
        nettyServer = createNettyWebServer(module);
        nettyServerClass.getDeclaredMethod("start").invoke(nettyServer);
    }

    public void stop() throws Exception {
        nettyServerClass.getDeclaredMethod("stop").invoke(nettyServer);
    }

    private Object createNettyWebServer(Object module) throws Exception {
        return nettyServerClass.getConstructor(moduleInterface).newInstance(module);
    }

    private Object createModule() throws Exception {
        Object moduleFactory = moduleFactoryClass.newInstance();
        return moduleFactoryClass.getMethod("create").invoke(moduleFactory);
    }

}
