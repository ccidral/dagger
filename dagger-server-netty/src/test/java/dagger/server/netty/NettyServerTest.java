package dagger.server.netty;

import dagger.Action;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.Get;
import dagger.handlers.Post;
import dagger.handlers.WebSocketClose;
import dagger.handlers.WebSocketOpen;
import dagger.http.HttpHeaderNames;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.module.DefaultModule;
import dagger.reactions.Ok;
import dagger.routes.ExactRoute;
import dagger.server.Server;
import de.roderick.weberknecht.*;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.fail;

public class NettyServerTest {

    private Server server;
    private Module module;

    @Before
    public void setUp() throws Exception {
        module = new DefaultModule();
        server = new NettyServer(8123, module);
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test(timeout = 2000)
    public void testSuccessfulGetRequest() throws Exception {
        on(get("/hello", new Action() {
            @Override
            public Reaction execute(Request request) {
                return new Reaction() {
                    @Override
                    public void execute(Request request, Response response) throws Exception {
                        try {
                            response.getOutputStream().write("Hello world".getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        response.setStatusCode(StatusCode.OK);
                        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                    }
                };
            }
        }));

        HttpResponse response = get("/hello");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello world", IOUtils.toString(response.getEntity().getContent()));
        assertEquals("text/plain", response.getEntity().getContentType().getValue());
    }

    @Test(timeout = 2000)
    public void testSuccessfulPostRequestWithBody() throws Exception {
        on(post("/greet", new Action() {
            @Override
            public Reaction execute(Request request) {
                return new Reaction() {
                    @Override
                    public void execute(Request request, Response response) throws Exception {
                        String body = IOUtils.toString(request.getInputStream());
                        try {
                            response.getOutputStream().write(("Hello " + body).getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        response.setStatusCode(StatusCode.OK);
                        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain");
                    }
                };
            }
        }));

        HttpResponse response = post("/greet", "John");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello John", IOUtils.toString(response.getEntity().getContent()));
        assertEquals("text/plain", response.getEntity().getContentType().getValue());
    }

    @Test(timeout = 2000)
    public void testExceptionWhileHandlingRequest() throws Exception {
        on(get("/hi", new Action() {
            @Override
            public Reaction execute(Request request) throws Exception {
                throw new Exception();
            }
        }));

        HttpResponse response = get("/hi");

        assertNotNull(response);
        assertEquals(500, response.getStatusLine().getStatusCode());
    }

    @Test(timeout = 2000)
    public void testExceptionWhileExecutingReaction() throws Exception {
        on(get("/hi", new Action() {
            @Override
            public Reaction execute(Request request) throws Exception {
                return new Reaction() {
                    @Override
                    public void execute(Request request, Response response) throws Exception {
                        throw new Exception();
                    }
                };
            }
        }));

        HttpResponse response = get("/hi");

        assertNotNull(response);
        assertEquals(500, response.getStatusLine().getStatusCode());
    }

    @Test(timeout = 2000)
    public void testOpenWebSocket() throws Exception {
        WebSocketServerHandler serverOpenHandler = new WebSocketServerHandler();
        on(wsopen("/greet", serverOpenHandler));

        serverOpenHandler.replyWith("Ready!");

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();
        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);

        client.connect();
        clientConnection.waitToOpen();

        String message = serverOpenHandler.waitForMessage();
        assertEquals("Empty message", "", message);

        String reply = clientConnection.waitForReply();
        assertEquals("Reply from server", "Ready!", reply);

        client.close();
    }

    @Test(timeout = 2000)
    public void testWebSocketMessage() throws Exception {
        WebSocketServerHandler serverMessageHandler = new WebSocketServerHandler();
        serverMessageHandler.replyWith("World");

        on(wsopen("/greet", new WebSocketServerHandler()));
        on(wsmessage("/greet", serverMessageHandler));

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();
        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);

        client.connect();
        clientConnection.waitToOpen();

        client.send("Hello");
        String messageReceivedFromClient = serverMessageHandler.waitForMessage();
        assertEquals("Message received from client", "Hello", messageReceivedFromClient);

        String reply = clientConnection.waitForReply();
        assertEquals("Reply from server", "World", reply);

        client.close();
    }

    @Test(timeout = 2000)
    public void testCloseWebSocket() throws Exception {
        WebSocketServerHandler serverCloseHandler = new WebSocketServerHandler();
        on(wsopen("/greet", new WebSocketServerHandler()));
        on(wsclose("/greet", serverCloseHandler));

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();
        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);

        client.connect();
        clientConnection.waitToOpen();

        client.close();
        clientConnection.waitToClose();

        String message = serverCloseHandler.waitForMessage();
        assertEquals("Empty message", "", message);

        client.close();
    }

    @Test(timeout = 2000)
    public void testDoNotAllowWebSocketOnRegularGetRoutes() throws Exception {
        on(get("/greet", new Greeting()));

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();
        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);

        try {
            client.connect();
        }
        catch(WebSocketException exception) {
            assertEquals("connection failed: 404 not found", exception.getMessage());
        }
    }

    @Test(timeout = 2000)
    public void testDoNotAcceptWebSocketRequestIfStatusCodeIsNot200() throws Exception {
        on(wsopen("/greet", new Action() {
            @Override
            public Reaction execute(Request request) throws Exception {
                return new Reaction() {
                    @Override
                    public void execute(Request request, Response response) throws Exception {
                        response.setStatusCode(StatusCode.SEE_OTHER);
                    }
                };
            }
        }));

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();
        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);

        try {
            client.connect();
            fail("Should not succeed");
        }
        catch(WebSocketException exception) {
            assertEquals("connection failed: unknown status code 303", exception.getMessage());
        }
    }

    private HttpResponse get(String uri) throws IOException {
        HttpClient client = new DefaultHttpClient();
        return client.execute(new HttpGet("http://localhost:8123" + uri));
    }

    private HttpResponse post(String uri, String body) throws IOException {
        HttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost("http://localhost:8123" + uri);
        httpPost.setEntity(new StringEntity(body));
        return client.execute(httpPost);
    }

    private void on(RequestHandler requestHandler) {
        module.add(requestHandler);
    }

    private RequestHandler get(String resourceName, Action action) {
        return new Get(new ExactRoute(resourceName), action);
    }

    private RequestHandler post(String resourceName, Action action) {
        return new Post(new ExactRoute(resourceName), action);
    }

    private RequestHandler wsopen(String resourceName, Action action) {
        return new WebSocketOpen(new ExactRoute(resourceName), action);
    }

    private RequestHandler wsmessage(String resourceName, Action action) {
        return new dagger.handlers.WebSocketMessage(new ExactRoute(resourceName), action);
    }

    private RequestHandler wsclose(String resourceName, Action action) {
        return new WebSocketClose(new ExactRoute(resourceName), action);
    }

    private static class Greeting implements Action {
        private String message;

        @Override
        public Reaction execute(Request request) throws Exception {
            return new Reaction() {
                @Override
                public void execute(Request request, Response response) throws Exception {
                    setMessage(IOUtils.toString(request.getInputStream()));
                    OutputStream outputStream = response.getOutputStream();
                    outputStream.write(("Hello " + message).getBytes());
                }
            };
        }

        private synchronized void setMessage(String newMessage) {
            this.message = newMessage;
            notifyAll();
        }

    }

    private static class WebSocketClientHandler implements WebSocketEventHandler {
        private final StringBuffer buffer = new StringBuffer();
        private final Object connectionStateMonitor = new Object();
        private boolean isOpen;

        @Override
        public void onOpen() {
            synchronized (connectionStateMonitor) {
                isOpen = true;
                connectionStateMonitor.notifyAll();
            }
        }

        @Override
        public void onClose() {
            synchronized (connectionStateMonitor) {
                isOpen = false;
                connectionStateMonitor.notifyAll();
            }
        }

        @Override
        public void onMessage(WebSocketMessage message) {
            synchronized (buffer) {
                buffer.append(message.getText());
                buffer.notifyAll();
            }
        }

        public String waitForReply() throws InterruptedException {
            synchronized (buffer) {
                if(buffer.length() == 0)
                    buffer.wait();
                return buffer.toString();
            }
        }

        public void waitToOpen() throws InterruptedException {
            synchronized (connectionStateMonitor) {
                if(!isOpen)
                    wait();
            }
        }

        public void waitToClose() throws InterruptedException {
            synchronized (connectionStateMonitor) {
                if(isOpen)
                    wait();
            }
        }
    }

    private class WebSocketServerHandler implements Action {

        private String message;
        private String reply;

        @Override
        public Reaction execute(Request request) throws Exception {
            synchronized (this) {
                this.message = IOUtils.toString(request.getInputStream());
                notifyAll();
            }

            if(reply == null)
                return new Ok("");

            return new Reaction() {
                @Override
                public void execute(Request request, Response response) throws Exception {
                    OutputStream outputStream = response.getOutputStream();
                    outputStream.write(reply.getBytes());
                    outputStream.flush();
                }
            };
        }

        public synchronized String waitForMessage() throws InterruptedException {
            if(this.message == null)
                wait();
            return message;
        }

        public void replyWith(String reply) {
            this.reply = reply;
        }

    }

}
