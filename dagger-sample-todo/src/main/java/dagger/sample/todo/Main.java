package dagger.sample.todo;

import dagger.Action;
import dagger.Module;
import dagger.module.DefaultModule;
import dagger.Reaction;
import dagger.handlers.Get;
import dagger.http.Request;
import dagger.lang.mime.DefaultMimeTypeGuesser;
import dagger.lang.mime.MimeTypeGuesser;
import dagger.reactions.StaticFile;
import dagger.routes.AnyRoute;
import dagger.server.Server;
import dagger.server.netty.NettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        final Module module = new DefaultModule();
        final MimeTypeGuesser mimeTypeGuesser = new DefaultMimeTypeGuesser();

        module.add(new Get(new AnyRoute(), new Action() {
            public Reaction execute(Request request) {
                logger.info("get "+request.getURI());
                return new StaticFile(request.getURI(), mimeTypeGuesser);
            }
        }));

        Server server = new NettyServer(8080, module);
        server.start();
    }

}
