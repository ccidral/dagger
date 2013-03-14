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
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

/**
 * Warning: WebSocket support is experimental.
 */
public class NettyWebSocketHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final Module module;

    private WebSocketServerHandshaker handshaker;
    private Map<Integer, FullHttpRequest> connections = new HashMap<>();

    public NettyWebSocketHandler(Module module) {
        this.module = module;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame)
            handleWebSocketFrame(context, (WebSocketFrame) msg);

        else if (msg instanceof FullHttpRequest) {
            FullHttpRequest nettyHttpRequest = (FullHttpRequest) msg;
            if(isWebSocket(nettyHttpRequest))
                handleWebSocketRequest(context, (FullHttpRequest) msg);
            else {
                nettyHttpRequest.retain();
                context.nextInboundMessageBuffer().add(msg);
            }
        }
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

        Request request = new NettyWebSocketRequest("", WebSocketOpen.METHOD, httpRequest);
        RequestHandler requestHandler = module.getHandlerFor(request);

        if (requestHandler instanceof ResourceNotFound) {
            sendHttpResponse(context, httpRequest, new DefaultFullHttpResponse(HTTP_1_1, NOT_FOUND));
            return;
        }

        Channel channel = context.channel();
        WebSocketServerHandshakerFactory wsFactory = new WebSocketServerHandshakerFactory(getWebSocketLocation(httpRequest), null, false);
        handshaker = wsFactory.newHandshaker(httpRequest);
        if (handshaker == null) {
            WebSocketServerHandshakerFactory.sendUnsupportedWebSocketVersionResponse(channel);
        }
        else {
            handshaker.handshake(channel, httpRequest);
            connections.put(channel.id(), httpRequest);
        }

        Reaction reaction = requestHandler.handle(request);

        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        reaction.execute(request, new NettyWebSocketResponse(outputStream));

        context.channel().write(new TextWebSocketFrame(Unpooled.copiedBuffer(outputStream.toByteArray())));
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + req.getUri();
    }

    private static void sendHttpResponse(ChannelHandlerContext context, FullHttpRequest request, FullHttpResponse response) {
        if (response.getStatus().code() != 200) {
            response.data().writeBytes(Unpooled.copiedBuffer(response.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(response, response.data().readableBytes());
        }

        ChannelFuture futureWrite = context.channel().write(response);
        if (!isKeepAlive(request) || response.getStatus().code() != 200)
            futureWrite.addListener(ChannelFutureListener.CLOSE);
    }

    private boolean isWebSocket(FullHttpRequest nettyHttpRequest) {
        String upgradeHeader = nettyHttpRequest.headers().get("Upgrade");
        return "WebSocket".equals(upgradeHeader);
    }

    private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
        if(frame instanceof TextWebSocketFrame)
            handleWebSocketMessage(context, (TextWebSocketFrame) frame);

        else if (frame instanceof PingWebSocketFrame)
            pong(context, frame);

        else if (frame instanceof CloseWebSocketFrame)
            closeWebSocket(context, (CloseWebSocketFrame) frame);

        else
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
    }

    private void handleWebSocketMessage(ChannelHandlerContext context, TextWebSocketFrame frame) throws Exception {
        String response = handleWebSocketEvent(frame.text(), WebSocketMessage.METHOD, context);
        context.channel().write(new TextWebSocketFrame(response));
    }

    private void pong(ChannelHandlerContext context, WebSocketFrame frame) {
        frame.data().retain();
        context.channel().write(new PongWebSocketFrame(frame.data()));
    }

    private void closeWebSocket(ChannelHandlerContext context, CloseWebSocketFrame frame) throws Exception {
        frame.retain();
        handshaker.close(context.channel(), frame);
        try {
            handleWebSocketEvent("", WebSocketClose.METHOD, context);
        } finally {
            connections.remove(context.channel().id());
        }
    }

    private String handleWebSocketEvent(String message, String method, ChannelHandlerContext context) throws Exception {
        FullHttpRequest nettyHttpRequest = connections.get(context.channel().id());
        Request request = new NettyWebSocketRequest(message, method, nettyHttpRequest);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new NettyWebSocketResponse(outputStream);
        RequestHandler requestHandler = module.getHandlerFor(request);
        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);
        return new String(outputStream.toByteArray());
    }

}