package dagger.server.netty;

import dagger.DaggerModule;
import dagger.server.DaggerServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class DaggerNettyServer implements DaggerServer {

    private final int port;
    private final DaggerModule daggerModule;

    private ServerBootstrap serverBootstrap;

    public DaggerNettyServer(int port, DaggerModule daggerModule) {
        this.port = port;
        this.daggerModule = daggerModule;
    }

    @Override
    public void start() {
        serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(new NioServerSocketChannel())
                    .childHandler(new HttpServerInitializer(daggerModule))
                    .localAddress(new InetSocketAddress(port));

            serverBootstrap.bind().sync().channel();

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        serverBootstrap.shutdown();
    }
}
