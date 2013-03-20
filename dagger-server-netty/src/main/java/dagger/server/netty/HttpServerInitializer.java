package dagger.server.netty;

import dagger.Module;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.*;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Module module;

    public HttpServerInitializer(Module module) {
        this.module = module;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline p = ch.pipeline();

        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
        p.addLast("encoder", new HttpResponseEncoder());
        // TODO find out why the compressor messes up with the websocket handler
        // p.addLast("deflater", new HttpContentCompressor());
        p.addLast("websocket", new NettyWebSocketHandler(module));
        p.addLast("handler", new HttpServerHandler(module));
    }

}
