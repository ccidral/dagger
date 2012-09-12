package dagger.server.netty;

import dagger.RequestHandler;
import dagger.RequestHandlers;
import dagger.Result;
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

    private final RequestHandlers requestHandlers;

    public HttpServerHandler(RequestHandlers requestHandlers) {
        this.requestHandlers = requestHandlers;
    }

    @Override
    public void messageReceived(ChannelHandlerContext context, Object msg) throws Exception {
        Request request = new NettyHttpRequestAdapter((HttpRequest) msg);
        RequestHandler requestHandler = requestHandlers.getHandlerFor(request);
        Result result = requestHandler.handle(request);

        HttpResponse nettyHttpResponse = new DefaultHttpResponse(HTTP_1_1, OK);
        Response response = new NettyHttpResponseAdapter(nettyHttpResponse);
        result.applyTo(response);

        nettyHttpResponse.setHeader(CONTENT_LENGTH, nettyHttpResponse.getContent().readableBytes());
        context.write(nettyHttpResponse);



        //HttpRequest request = (HttpRequest) msg;
        //StringBuilder buf = new StringBuilder();
        //buf.append("Hello world");
        //writeResponse(request, context, buf);
    }
    /*
    private void writeResponse(HttpRequest request, ChannelHandlerContext ctx, StringBuilder buf) {
        //boolean keepAlive = isKeepAlive(request);

        HttpResponse response = new DefaultHttpResponse(HTTP_1_1, OK);
        response.setContent(Unpooled.copiedBuffer(buf.toString(), CharsetUtil.UTF_8));
        response.setHeader(CONTENT_LENGTH, response.getContent().readableBytes());
        //response.setHeader(CONTENT_TYPE, "text/plain; charset=UTF-8");

        //if (keepAlive) {

        //    response.setHeader(CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        //}

        ctx.write(response);

        //if (!keepAlive) {
        //    future.addListener(ChannelFutureListener.CLOSE);
        //}
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
     */
}