package dagger.server.netty;

import dagger.Reaction;
import dagger.RequestHandler;
import dagger.DaggerModule;
import dagger.http.Request;
import dagger.http.Response;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.DefaultHttpResponse;
import io.netty.handler.codec.http.HttpRequest;
import io.netty.handler.codec.http.HttpResponse;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final DaggerModule daggerModule;

    public HttpServerHandler(DaggerModule daggerModule) {
        this.daggerModule = daggerModule;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        Request request = new NettyRequest((HttpRequest) msg);
        RequestHandler requestHandler = daggerModule.getHandlerFor(request);
        Reaction reaction = requestHandler.handle(request);

        HttpResponse nettyHttpResponse = new DefaultHttpResponse(HTTP_1_1, OK);
        Response response = new NettyResponse(nettyHttpResponse);
        reaction.execute(response);

        nettyHttpResponse.setHeader(CONTENT_LENGTH, nettyHttpResponse.getContent().readableBytes());
        context.write(nettyHttpResponse);
    }

}