package dagger.server.netty;

import dagger.Module;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.Attribute;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {

    private final Module module;
    private long channelCounter = 0;

    public HttpServerInitializer(Module module) {
        this.module = module;
    }

    @Override
    public void initChannel(SocketChannel channel) throws Exception {
        assignChannelIdTo(channel);
        configurePipeline(channel.pipeline());
    }

    private void configurePipeline(ChannelPipeline p) {
        p.addLast("decoder", new HttpRequestDecoder());
        p.addLast("aggregator", new HttpObjectAggregator(1024 * 1024));
        p.addLast("encoder", new HttpResponseEncoder());
        // TODO find out why the compressor messes up with the websocket handler
        // p.addLast("deflater", new HttpContentCompressor());
        p.addLast("websocket", new NettyWebSocketHandler(module));
        p.addLast("handler", new HttpServerHandler(module));
    }

    private void assignChannelIdTo(SocketChannel ch) {
        Attribute<Long> channelId = ch.attr(ChannelAttributes.CHANNEL_ID);
        channelId.setIfAbsent(incrementChannelCounter());
    }

    private synchronized long incrementChannelCounter() {
        return ++channelCounter;
    }

}
