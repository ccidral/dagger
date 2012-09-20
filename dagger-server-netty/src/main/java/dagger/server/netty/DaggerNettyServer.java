package dagger.server.netty;

import dagger.Module;
import dagger.server.DaggerServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.socket.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

public class DaggerNettyServer implements DaggerServer {

    private final int port;
    private final Module module;
    private final Logger logger;

    private ServerBootstrap serverBootstrap;

    public DaggerNettyServer(int port, Module module) {
        this.port = port;
        this.module = module;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void start() {
        serverBootstrap = new ServerBootstrap();
        try {
            serverBootstrap.group(new NioEventLoopGroup(), new NioEventLoopGroup())
                    .channel(new NioServerSocketChannel())
                    .childHandler(new HttpServerInitializer(module))
                    .localAddress(new InetSocketAddress(port));

            serverBootstrap.bind().sync().channel();

            logger.info("Listening on port {}", port);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void stop() {
        serverBootstrap.shutdown();
    }

}
