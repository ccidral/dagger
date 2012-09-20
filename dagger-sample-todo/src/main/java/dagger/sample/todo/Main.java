package dagger.sample.todo;

import dagger.Action;
import dagger.DaggerModule;
import dagger.DefaultDaggerModule;
import dagger.Reaction;
import dagger.handlers.Get;
import dagger.http.Request;
import dagger.lang.mime.DefaultMimeTypeGuesser;
import dagger.reactions.StaticFile;
import dagger.resource.AnyResourceName;
import dagger.server.DaggerServer;
import dagger.server.netty.DaggerNettyServer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Main {

    private static final Logger logger = LoggerFactory.getLogger(Main.class);

    public static void main(String[] args) {
        DaggerModule module = new DefaultDaggerModule();
        module.add(new Get(new AnyResourceName(), new Action() {
            public Reaction execute(Request request) {
                logger.info("get "+request.getResource());
                return new StaticFile(request.getResource(), new DefaultMimeTypeGuesser());
            }
        }));

        DaggerServer server = new DaggerNettyServer(8080, module);
        server.start();
    }

}
