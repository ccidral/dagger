package dagger.servlet3.sample;

import dagger.*;
import dagger.http.Request;
import dagger.module.DefaultModule;
import dagger.module.DefaultModuleBuilder;
import dagger.reactions.Ok;
import dagger.routes.WildcardRouteFactory;

public class SampleModuleFactory implements ModuleFactory {

    @Override
    public Module create() {
        Module module = new DefaultModule();
        RouteFactory routeFactory = new WildcardRouteFactory();
        ModuleBuilder builder = new DefaultModuleBuilder(module, routeFactory);
        builder.get("/", new Action() {
            @Override
            public Reaction execute(Request request) throws Exception {
                return new Ok(
                    "<html><body>" +
                        "<h1>Welcome</h1>" +
                        "<p>This is an example of a Dagger app running on a servlet container.</p>" +
                        "<p><a href='greeting'>Click here</a> to see the greeting page.</p>" +
                    "</body></html>", "text/html");
            }
        });
        builder.get("/greeting", new Action() {
            @Override
            public Reaction execute(Request request) throws Exception {
                return new Ok("<html><body><h1>Hello world</h1></body></html>", "text/html");
            }
        });
        return module;
    }

}
