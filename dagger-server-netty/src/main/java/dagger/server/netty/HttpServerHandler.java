package dagger.server.netty;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.http.Request;
import dagger.http.Response;
import dagger.lang.time.SystemClock;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;
import io.netty.util.ReferenceCountUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import static io.netty.handler.codec.http.HttpHeaders.Names.CONTENT_LENGTH;
import static io.netty.handler.codec.http.HttpResponseStatus.INTERNAL_SERVER_ERROR;
import static io.netty.handler.codec.http.HttpResponseStatus.OK;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class HttpServerHandler extends ChannelInboundHandlerAdapter {

    private final Module module;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public HttpServerHandler(Module module) {
        this.module = module;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof HttpRequest) {
            FullHttpResponse nettyHttpResponse;

            try {
                nettyHttpResponse = processRequest((FullHttpRequest) msg);
            } catch (Exception e) {
                logger.error("Error while handling request", e);
                nettyHttpResponse = createErrorResponse();
            }

            nettyHttpResponse.headers().set(CONTENT_LENGTH, nettyHttpResponse.content().readableBytes());
            context.write(nettyHttpResponse);
        }

        ReferenceCountUtil.release(msg);

        super.channelRead(context, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error("Exception caught", cause);
        context.close();
    }

    private FullHttpResponse createErrorResponse() {
        return new DefaultFullHttpResponse(HTTP_1_1, INTERNAL_SERVER_ERROR);
    }

    private FullHttpResponse processRequest(FullHttpRequest msg) throws Exception {
        Request request = new NettyRequest(msg);
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