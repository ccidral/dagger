package dagger.server.netty;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import dagger.lang.time.SystemClock;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundMessageHandlerAdapter;
import io.netty.handler.codec.http.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final Module module;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public HttpServerHandler(Module module) {
        this.module = module;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        FullHttpResponse nettyHttpResponse;

        try {
            nettyHttpResponse = processRequest((FullHttpRequest) msg, (InetSocketAddress) context.channel().localAddress());
        } catch (Exception e) {
            logger.error("Error while handling request", e);
            nettyHttpResponse = createErrorResponse();
        }

        nettyHttpResponse.headers().set(CONTENT_LENGTH, nettyHttpResponse.data().readableBytes());
        context.write(nettyHttpResponse);
    }

    private FullHttpResponse createErrorResponse() {
        return new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
    }

    private FullHttpResponse processRequest(FullHttpRequest msg, InetSocketAddress localAddress) throws Exception {
        Request request = new NettyRequest(localAddress.getHostName(), localAddress.getPort(), msg);
        Reaction reaction = handleRequest(request);
        return executeReaction(reaction, request);
    }

    private FullHttpResponse executeReaction(Reaction reaction, Request request) throws Exception {
        FullHttpResponse nettyHttpResponse = new DefaultFullHttpResponse(HTTP_1_1, OK);
        Response response = new NettyResponse(nettyHttpResponse, new SystemClock());
        reaction.execute(request, response);
        return nettyHttpResponse;
    }

    private Reaction handleRequest(Request request) throws Exception {
        logger.debug("{} {}", request.getMethod(), request.getURI());
        RequestHandler requestHandler = module.getHandlerFor(request);
        return requestHandler.handle(request);
    }

}