package dagger.module;

import dagger.*;

public class DefaultModuleBuilder implements ModuleBuilder {

    private final Module module;
    private final RequestHandlerFactory requestHandlerFactory;

    public DefaultModuleBuilder(Module module, RequestHandlerFactory requestHandlerFactory) {
        this.module = module;
        this.requestHandlerFactory = requestHandlerFactory;
    }

    @Override
    public void get(String route, Action action) {
        RequestHandler get = requestHandlerFactory.createGet(route, action);
        module.add(get);
    }

    @Override
    public void put(String route, Action action) {
        RequestHandler handler = requestHandlerFactory.createPut(route, action);
        module.add(handler);
    }

    @Override
    public void post(String route, Action action) {
        RequestHandler handler = requestHandlerFactory.createPost(route, action);
        module.add(handler);
    }

}
