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
    public void get(String uri, Action action) {
        RequestHandler get = requestHandlerFactory.createGet(uri, action);
        module.add(get);
    }

}
