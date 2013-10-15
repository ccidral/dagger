package dagger.server.netty;

import dagger.Action;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.*;
import dagger.http.HttpHeaderNames;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.module.DefaultModule;
import dagger.routes.ExactRoute;
import dagger.server.Server;
import dagger.websocket.DefaultWebSocketSessionFactory;
import dagger.websocket.WebSocketSession;
import dagger.websocket.WebSocketSessionHandler;
import de.roderick.weberknecht.*;
import de.roderick.weberknecht.WebSocket;
import de.roderick.weberknecht.WebSocketMessage;
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
import java.net.URI;
import java.net.URISyntaxException;

import static junit.framework.Assert.assertEquals;
import static junit.framework.Assert.assertNotNull;

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
    public void test_open_websocket() throws Throwable {
        WebSocketServerHandler server = new WebSocketServerHandler();
        on(websocket("/greet", server));

        WebSocketClientHandler client = new WebSocketClientHandler();
        WebSocket websocket = connectToWebSocket("ws://localhost:8123/greet", client);

        server.waitToOpen();
        client.waitToOpen();

        websocket.close();
    }

    @Test(timeout = 2000)
    public void test_send_message_to_client_on_websocket_open() throws Throwable {
        WebSocketClientHandler client = new WebSocketClientHandler();
        WebSocketServerHandler server = new WebSocketServerHandler();

        server.setMessageToSendOnOpen("Hello");

        on(websocket("/greet", server));

        WebSocket websocket = connectToWebSocket("ws://localhost:8123/greet", client);

        server.waitToOpen();
        client.waitToOpen();

        String message = client.waitForMessage();
        assertEquals("Message from server", "Hello", message);

        websocket.close();
    }

    @Test(timeout = 2000)
    public void test_websocket_message_from_client_to_server() throws Throwable {
        WebSocketClientHandler client = new WebSocketClientHandler();
        WebSocketServerHandler server = new WebSocketServerHandler();

        on(websocket("/greet", server));

        WebSocket websocket = connectToWebSocket("ws://localhost:8123/greet", client);

        server.waitToOpen();
        client.waitToOpen();

        websocket.send("Hello Server");

        String message = server.waitForMessage();
        assertEquals("Message from client", "Hello Server", message);

        websocket.close();
    }

    @Test(timeout = 2000)
    public void test_websocket_message_from_server_to_client() throws Throwable {
        WebSocketClientHandler client = new WebSocketClientHandler();
        WebSocketServerHandler server = new WebSocketServerHandler();

        on(websocket("/greet", server));

        WebSocket websocket = connectToWebSocket("ws://localhost:8123/greet", client);

        server.waitToOpen();
        client.waitToOpen();

        server.sendMessage("Hello Client");

        String message = client.waitForMessage();
        assertEquals("Message from server", "Hello Client", message);

        websocket.close();
    }

    @Test(timeout = 2000)
    public void test_close_websocket() throws Throwable {
        WebSocketServerHandler server = new WebSocketServerHandler();
        on(websocket("/greet", server));

        WebSocketClientHandler client = new WebSocketClientHandler();
        WebSocket websocket = connectToWebSocket("ws://localhost:8123/greet", client);

        server.waitToOpen();
        client.waitToOpen();

        websocket.close();

        server.waitToClose();
        client.waitToClose();
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

    private WebSocket connectToWebSocket(String url, WebSocketClientHandler clientConnection) throws WebSocketException, URISyntaxException {
        WebSocket client = new WebSocketConnection(new URI(url));
        client.setEventHandler(clientConnection);
        client.connect();
        return client;
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

    private RequestHandler websocket(String resourceName, WebSocketSessionHandler webSocketSessionHandler) {
        return new dagger.handlers.WebSocket(new ExactRoute(resourceName), webSocketSessionHandler, new DefaultWebSocketSessionFactory());
    }

    private static class WebSocketServerHandler implements WebSocketSessionHandler {

        private final Object openLock = new Object();
        private final Object messageLock = new Object();
        private final Object closeLock = new Object();

        private boolean isOpen;
        private boolean isClosed;

        private String messageFromClient;
        private String messageToSendToClientOnOpen;
        private WebSocketSession webSocketSession;

        @Override
        public void onOpen(Request request, WebSocketSession webSocketSession) {
            this.webSocketSession = webSocketSession;

            synchronized (openLock) {
                isOpen = true;
                openLock.notifyAll();
            }

            if(messageToSendToClientOnOpen != null)
                webSocketSession.write(messageToSendToClientOnOpen);
        }

        @Override
        public void onClose(Request request) {
            synchronized (closeLock) {
                isClosed = true;
                closeLock.notifyAll();
            }
        }

        @Override
        public void onMessage(Request request, WebSocketSession session, String message) {
            synchronized (messageLock) {
                this.messageFromClient = message;
                messageLock.notifyAll();
            }
        }

        public void setMessageToSendOnOpen(String messageToSendToClientOnOpen) {
            this.messageToSendToClientOnOpen = messageToSendToClientOnOpen;
        }

        public void sendMessage(String message) {
            webSocketSession.write(message);
        }

        public void waitToOpen() throws InterruptedException {
            synchronized (openLock) {
                if(!isOpen)
                    openLock.wait();
            }
        }

        public String waitForMessage() throws InterruptedException {
            synchronized (messageLock) {
                if(messageFromClient == null)
                    messageLock.wait();
            }
            return messageFromClient;
        }

        public void waitToClose() throws InterruptedException {
            synchronized (closeLock) {
                if(!isClosed)
                    closeLock.wait();
            }
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

        public String waitForMessage() throws InterruptedException {
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

}
