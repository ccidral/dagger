package dagger.server.netty;

import dagger.*;
import dagger.handlers.Get;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.resource.ExactResourceName;
import dagger.server.DaggerServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.IOException;

import static junit.framework.Assert.assertEquals;

public class DaggerNettyServerTest {

    private DaggerServer server;
    private DaggerModule module;

    @Before
    public void setUp() throws Exception {
        module = new DefaultDaggerModule();
        server = new DaggerNettyServer(8123, module);
        server.start();
    }

    @After
    public void tearDown() {
        server.stop();
    }

    @Test
    public void test() throws Exception {
        on(get("/hello", new Action() {
            @Override
            public Reaction execute() {
                return new Reaction() {
                    @Override
                    public void execute(Response response) {
                        try {
                            response.getOutputStream().write("Hello world".getBytes());
                        } catch (IOException e) {
                            throw new RuntimeException(e);
                        }
                        response.setStatusCode(StatusCode.OK);
                        response.setContentType("text/plain");
                    }
                };
            }
        }));

        HttpResponse response = request("/hello");

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello world", IOUtils.toString(response.getEntity().getContent()));
        assertEquals("text/plain", response.getEntity().getContentType().getValue());
    }

    private HttpResponse request(String uri) throws IOException {
        HttpClient client = new DefaultHttpClient();
        return client.execute(new HttpGet("http://localhost:8123" + uri));
    }

    private void on(RequestHandler requestHandler) {
        module.add(requestHandler);
    }

    private RequestHandler get(String resourceName, Action action) {
        return new Get(new ExactResourceName(resourceName), action);
    }

}
