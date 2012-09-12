package dagger.server.netty;

import dagger.RequestHandlers;
import dagger.server.DaggerServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;

import java.net.InetSocketAddress;

public class DaggerNettyServer implements DaggerServer {

    private final int port;
    private final RequestHandlers requestHandlers;

    private ServerBootstrap serverBootstrap;

    public DaggerNettyServer(int port, RequestHandlers requestHandlers) {
        this.port = port;
        this.requestHandlers = requestHandlers;
    }

    @Override
    public void start() {
        serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(new NioServerSocketChannel())
                    .childHandler(new HttpServerInitializer(requestHandlers))
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
