package dagger.sample.todo;

import dagger.Action;
import dagger.Module;
import dagger.ModuleBuilder;
import dagger.Reaction;
import dagger.http.Request;
import dagger.lang.mime.DefaultMimeTypeGuesser;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.module.DefaultModule;
import dagger.module.DefaultModuleBuilder;
import dagger.reactions.ResourceFile;
import dagger.routes.WildcardRouteFactory;
import dagger.server.Server;
import dagger.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        Module module = new DefaultModule();
        ModuleBuilder builder = new DefaultModuleBuilder(module, new WildcardRouteFactory());
        final MimeTypeGuesser mimeTypeGuesser = new DefaultMimeTypeGuesser();

        builder.get("/*", new Action() {
            public Reaction execute(Request request) {
                logger.info("get " + request.getURI());
                return new ResourceFile(request.getURI(), mimeTypeGuesser);
            }
        });

        Server server = new NettyServer(8080, module);
        server.start();
    }

}
