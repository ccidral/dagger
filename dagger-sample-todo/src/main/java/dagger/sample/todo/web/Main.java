package dagger.sample.todo.web;

import dagger.Module;
import dagger.ModuleFactory;
import dagger.server.Server;
import dagger.server.netty.NettyServer;

public class Main {

    public static void main(String[] args) {
        ModuleFactory moduleFactory = new TodoModuleFactory();
        Module module = moduleFactory.create();
        Server server = new NettyServer(8080, module);
        server.start();
    }

}
