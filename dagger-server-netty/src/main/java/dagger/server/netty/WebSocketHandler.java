package dagger.server.netty;

import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.ResourceNotFound;
import dagger.http.Request;
import dagger.http.Response;
import io.netty.buffer.Unpooled;
import io.netty.channel.*;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.util.CharsetUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import static io.netty.handler.codec.http.HttpHeaders.Names.HOST;
import static io.netty.handler.codec.http.HttpHeaders.isKeepAlive;
import static io.netty.handler.codec.http.HttpHeaders.setContentLength;
import static io.netty.handler.codec.http.HttpMethod.GET;
import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.HTTP_1_1;

public class WebSocketHandler extends ChannelInboundMessageHandlerAdapter<Object> {

    private final Module module;
    private final Logger logger = LoggerFactory.getLogger(getClass());

    private WebSocketServerHandshaker handshaker;
    private Map<Integer, WebSocketConnection> connections = new HashMap<>();

    public WebSocketHandler(Module module) {
        this.module = module;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        if (msg instanceof WebSocketFrame) {
            handleWebSocketFrame(context, (WebSocketFrame) msg);
        }
        else if (msg instanceof FullHttpRequest) {
            FullHttpRequest nettyHttpRequest = (FullHttpRequest) msg;
            if(isWebSocket(nettyHttpRequest))
                handleWebSocketRequest(context, (FullHttpRequest) msg);
            else {
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

        Request request = new NettyRequest(httpRequest);
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
            connections.put(channel.id(), new WebSocketConnection(channel, httpRequest));
        }
    }

    private String getWebSocketLocation(FullHttpRequest req) {
        return "ws://" + req.headers().get(HOST) + req.getUri();
    }

    private static void sendHttpResponse(ChannelHandlerContext ctx, FullHttpRequest req, FullHttpResponse res) {
        if (res.getStatus().code() != 200) {
            res.data().writeBytes(Unpooled.copiedBuffer(res.getStatus().toString(), CharsetUtil.UTF_8));
            setContentLength(res, res.data().readableBytes());
        }

        ChannelFuture f = ctx.channel().write(res);
        if (!isKeepAlive(req) || res.getStatus().code() != 200) {
            f.addListener(ChannelFutureListener.CLOSE);
        }
    }

    private boolean isWebSocket(FullHttpRequest nettyHttpRequest) {
        String upgradeHeader = nettyHttpRequest.headers().get("Upgrade");
        return "WebSocket".equals(upgradeHeader);
    }

    private void handleWebSocketFrame(ChannelHandlerContext context, WebSocketFrame frame) throws Exception {
        if (frame instanceof CloseWebSocketFrame) {
            frame.retain();
            handshaker.close(context.channel(), (CloseWebSocketFrame) frame);
            return;
        }
        if (frame instanceof PingWebSocketFrame) {
            frame.data().retain();
            context.channel().write(new PongWebSocketFrame(frame.data()));
            return;
        }
        if (!(frame instanceof TextWebSocketFrame)) {
            throw new UnsupportedOperationException(String.format("%s frame types not supported", frame.getClass().getName()));
        }

        WebSocketConnection connection = connections.get(context.channel().id());
        TextWebSocketFrame textFrame = (TextWebSocketFrame) frame;

        Request request = new NettyWebSocketRequest(textFrame, connection.request);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Response response = new NettyWebSocketResponse(outputStream);
        RequestHandler requestHandler = module.getHandlerFor(request);

        Reaction reaction = requestHandler.handle(request);
        reaction.execute(request, response);

        String text = new String(outputStream.toByteArray());
        context.channel().write(new TextWebSocketFrame(text));
    }

    private class WebSocketConnection {

        private final Channel channel;
        private final FullHttpRequest request;

        public WebSocketConnection(Channel channel, FullHttpRequest request) {
            this.channel = channel;
            this.request = request;
        }

    }
}