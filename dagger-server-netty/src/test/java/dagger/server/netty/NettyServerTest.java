package dagger.server.netty;

import dagger.Action;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.HttpMethodRequestHandler;
import dagger.http.*;
import dagger.mime.MimeType;
import dagger.module.DefaultModule;
import dagger.reactions.Ok;
import dagger.routes.ExactRoute;
import dagger.server.Server;
import dagger.websocket.DefaultWebSocketSessionFactory;
import dagger.websocket.WebSocketSession;
import dagger.websocket.WebSocketSessionHandler;
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
    public void test_successful_get_request() throws Exception {
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
                        response.setHeader(HttpHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN);
                    }
                };
            }
        }));

        HttpResponse response = get("/hello");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello world", IOUtils.toString(response.getEntity().getContent()));
        assertEquals(MimeType.TEXT_PLAIN, response.getEntity().getContentType().getValue());
    }

    @Test(timeout = 2000)
    public void test_successful_post_request_with_body() throws Exception {
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
                        response.setHeader(HttpHeader.CONTENT_TYPE, MimeType.TEXT_PLAIN);
                    }
                };
            }
        }));

        HttpResponse response = post("/greet", "John");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello John", IOUtils.toString(response.getEntity().getContent()));
        assertEquals(MimeType.TEXT_PLAIN, response.getEntity().getContentType().getValue());
    }

    @Test(timeout = 2000)
    public void test_exception_while_handling_request() throws Exception {
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
    public void test_exception_while_executing_reaction() throws Exception {
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
    public void test_get_request_url() throws Exception {
        final StringBuffer requestUrl = new StringBuffer();

        on(get("/hello", new Action() {
            @Override
            public Reaction execute(Request request) {
                requestUrl.append(request.getRequestURL());
                return new Ok();
            }
        }));

        get("/hello");

        assertEquals("http://localhost:8123/hello", requestUrl.toString());
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
        return new HttpMethodRequestHandler(HttpMethod.GET, new ExactRoute(resourceName), action);
    }

    private RequestHandler post(String resourceName, Action action) {
        return new HttpMethodRequestHandler(HttpMethod.POST, new ExactRoute(resourceName), action);
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
        public void onOpen(WebSocketSession webSocketSession) {
            this.webSocketSession = webSocketSession;

            synchronized (openLock) {
                isOpen = true;
                openLock.notifyAll();
            }

            if(messageToSendToClientOnOpen != null)
                webSocketSession.write(messageToSendToClientOnOpen);
        }

        @Override
        public void onClose(WebSocketSession webSocketSession) {
            synchronized (closeLock) {
                isClosed = true;
                closeLock.notifyAll();
            }
        }

        @Override
        public void onMessage(String message, WebSocketSession session) {
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
