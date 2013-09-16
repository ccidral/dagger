package dagger.server.netty;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.handlers.WebSocketClose;
import dagger.handlers.WebSocketMessage;
import dagger.handlers.WebSocketOpen;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpResponseStatus;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;


/**
 * Warning: WebSocket support is experimental. Crappy code ahead.
 */

public class NettyWebSocketHandler extends ChannelInboundHandlerAdapter {

    private final Module module;

    private WebSocketServerHandshaker handshaker;
    private Map<Long, FullHttpRequest> connections = new HashMap<>();
    private final Logger logger = LoggerFactory.getLogger(getClass());

    public NettyWebSocketHandler(Module module) {
        this.module = module;
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext context) {
        context.flush();
    }

    @Override
    public void channelRead(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame)  {
            handleWebSocketFrame(context, (WebSocketFrame) msg);
        }
        else if (msg instanceof FullHttpRequest) {
            FullHttpRequest nettyHttpRequest = (FullHttpRequest) msg;
            if(isWebSocket(nettyHttpRequest))
                handleWebSocketRequest(context, (FullHttpRequest) msg);
            else
                nettyHttpRequest.retain();
            }
        super.channelRead(context, msg);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext context, Throwable cause) {
        logger.error("Exception caught", cause);
        context.close();
    }

    private void handleWebSocketRequest(ChannelHandlerContext context, FullHttpRequest httpRequest) throws Exception {
        if (!httpRequest.getDecoderResult().isSuccess()) {
            sendHttpResponse(context, httpRequest, new DefaultFullHttpResponse(HTTP_1_1, BAD_REQUEST));
            return;
        }
        if (!GET.equals(httpRequest.getMethod())) {
            sendHttpResponse(context, httpRequest, new DefaultFullHttpResponse(HTTP_1_1, FORBIDDEN));
            return;
        }


        // Find a request handler...
        Request request = new NettyWebSocketRequest("", WebSocketOpen.METHOD, httpRequest);
        RequestHandler requestHandler = module.getHandlerFor(request);


        // No handler found for this request, returning 404 NOT FOUND.
        if (requestHandler instanceof ResourceNotFound) {
            sendHttpResponse(context, httpRequest, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }


        // There is a handler, let it handle the request...
        WebSocketOutputStream responseOutputStream = new WebSocketOutputStream();
        NettyWebSocketResponse response = new NettyWebSocketResponse(responseOutputStream);

        // ... also let's execute the reaction
        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);


        // Execution resulted in failure, which means that we cannot make websocket connection.
        if(!StatusCode.OK.equals(response.getStatusCode())) {
            HttpResponseStatus status = HttpResponseStatus.valueOf(response.getStatusCode().getNumber());
            sendHttpResponse(context, httpRequest, new DefaultFullHttpResponse(HTTP_1_1, status));
            return;
        }

        // Everything is ok, connection will start...
        if(!handshake(context, httpRequest)) {
            //... unless the handshake fails.
            return;
        }

        context.channel().closeFuture().addListener(new ChannelFutureListener() {
            @Override
            public void operationComplete(ChannelFuture channelFuture) throws Exception {
                Channel channel = channelFuture.channel();
                logger.debug("Channel closed  ~  channel={} cause={}", channel.attr(ChannelAttributes.CHANNEL_ID).get(), channelFuture.cause());
                closeWebSocket(channel, new CloseWebSocketFrame());
            }
        });

        // Write the contents of the response to the websocket channel.
        responseOutputStream.setChannel(context.channel());
        responseOutputStream.flush();
    }

    private boolean handshake(ChannelHandlerContext context, FullHttpRequest httpRequest) {
        final Channel channel = context.channel();
        String websocketLocation = getWebSocketLocation(httpRequest);
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(websocketLocation, null, false);
        handshaker = wsFactory.newHandshaker(httpRequest);

        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(channel);
            return false;
        }

        handshaker.handshake(channel, httpRequest);
        connections.put(channel.attr(ChannelAttributes.CHANNEL_ID).get(), httpRequest);
        return true;
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + req.getUri();
    }

    private static void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse response) {
        if (response.getStatus().code() != 200) {
            response.content().writeBytes(Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(response, response.content().readableBytes());
        }

        ChannelFuture futureWrite = context.channel().write(response);
        if (!isKeepAlive(request) || response.getStatus().code() != 200)
            futureWrite.addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isWebSocket(FullHttpRequest nettyHttpRequest) {
        String upgradeHeader = nettyHttpRequest.headers().get("Upgrade");
        return "websocket".equalsIgnoreCase(upgradeHeader);
    }

    private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
        if(frame instanceof TextWebSocketFrame)
            handleWebSocketMessage(context, (TextWebSocketFrame) frame);

        else if (frame instanceof PingWebSocketFrame)
            pong(context, frame);

        else if (frame instanceof CloseWebSocketFrame)
            closeWebSocket(context.channel(), (CloseWebSocketFrame) frame);

        else
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

    private void handleWebSocketMessage(ChannelHandlerContext context, TextWebSocketFrame frame) throws Exception {
        handleWebSocketEvent(frame.text(), WebSocketMessage.METHOD, context.channel());
    }

    private void pong(ChannelHandlerContext context, WebSocketFrame frame) {
        logger.debug("Ping  ~  channel={}", context.channel().attr(ChannelAttributes.CHANNEL_ID).get());
        frame.content().retain();
        context.channel().write(new PongWebSocketFrame(frame.content()));
    }

    private void closeWebSocket(Channel channel, CloseWebSocketFrame frame) throws Exception {
        frame.retain();
        handshaker.close(channel, frame);
        try {
            handleWebSocketEvent("", WebSocketClose.METHOD, channel);
        } finally {
            connections.remove(channel.attr(ChannelAttributes.CHANNEL_ID).get());
        }
    }

    private void handleWebSocketEvent(String message, String method, Channel channel) throws Exception {
        FullHttpRequest nettyHttpRequest = connections.get(channel.attr(ChannelAttributes.CHANNEL_ID).get());
        Request request = new NettyWebSocketRequest(message, method, nettyHttpRequest);
        Response response = new NettyWebSocketResponse(channel);
        RequestHandler requestHandler = module.getHandlerFor(request);
        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);
    }

}