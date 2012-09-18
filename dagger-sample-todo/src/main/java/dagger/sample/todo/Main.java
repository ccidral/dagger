package dagger.sample.todo;

import dagger.Action;
import dagger.DaggerModule;
import dagger.DefaultDaggerModule;
import dagger.Reaction;
import dagger.handlers.Get;
import dagger.reactions.StaticFile;
import dagger.resource.ExactResourceName;
import dagger.server.DaggerServer;
import dagger.server.netty.DaggerNettyServer;

public class Main {

    public static void main(String[] args) {
        DaggerModule module = new DefaultDaggerModule();
        module.add(new Get(new ExactResourceName("/"), new Action() {
            public Reaction execute() {
                return new StaticFile("index.html", "text/html");
            }
        }));

        DaggerServer server = new DaggerNettyServer(8080, module);
        server.start();
    }

}
