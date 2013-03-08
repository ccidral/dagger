package dagger.server.netty;

import dagger.Action;
import dagger.Module;
import dagger.Reaction;
import dagger.RequestHandler;
import dagger.handlers.Get;
import dagger.handlers.Post;
import dagger.http.HttpHeaderNames;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.module.DefaultModule;
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
                        String body = IOUtils.toString(request.getBody());
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
    public void testWebSocket() throws Exception {
        on(ws("/greet", new Greeting()));

        WebSocketClientHandler clientConnection = new WebSocketClientHandler();

        WebSocket client = new WebSocketConnection(new URI("ws://localhost:8123/greet"));
        client.setEventHandler(clientConnection);
        client.connect();
        client.send("World");

        String message = clientConnection.waitForMessage();
        assertEquals("Message received by the websocket server", "Hello World", message);
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

    private RequestHandler ws(String resourceName, Action action) {
        return new dagger.handlers.WebSocket(new ExactRoute(resourceName), action);
    }

    private static class Greeting implements Action {
        @Override
        public Reaction execute(Request request) throws Exception {
            return new Reaction() {
                @Override
                public void execute(Request request, Response response) throws Exception {
                    String body = IOUtils.toString(request.getBody());
                    OutputStream outputStream = response.getOutputStream();
                    outputStream.write(("Hello " + body).getBytes());
                }
            };
        }
    }

    private static class WebSocketClientHandler implements WebSocketEventHandler {
        private final StringBuffer buffer = new StringBuffer();

        @Override public void onOpen() {
        }

        @Override public void onClose() {
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
    }
}
