package dagger.server.netty;

import dagger.Module;
import dagger.server.Server;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class NettyServer implements Server {

    private final int port;
    private final Module module;
    private final Logger logger;

    private EventLoopGroup bossGroup;
    private EventLoopGroup workerGroup;

    public NettyServer(Module module) {
        this(8080, module);
    }

    public NettyServer(int port, Module module) {
        this.port = port;
        this.module = module;
        this.logger = LoggerFactory.getLogger(getClass());
    }

    @Override
    public void start() {
        bossGroup = new NioEventLoopGroup();
        workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new HttpServerInitializer(module));

            bootstrap.bind(port).sync();
        } catch (InterruptedException e) {
            logger.error("Error during bootstrap", e);
        }

    }

    @Override
    public void stop() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

}
