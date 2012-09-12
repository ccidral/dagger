package dagger.server.netty;

import dagger.*;
import dagger.handlers.Get;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.resourcematchers.ExactResourceName;
import dagger.server.DaggerServer;
import org.apache.commons.io.IOUtils;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class DaggerNettyServerTest {

    private DaggerServer server;
    private RequestHandlers requestHandlers;

    @Before
    public void setUp() throws Exception {
        requestHandlers = new DefaultRequestHandlers();
        server = new DaggerNettyServer(8123, requestHandlers);
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
            public Result execute() {
                return new Result() {
                    @Override
                    public void applyTo(Response response) {
                        response.write("Hello world");
                        response.setStatusCode(StatusCode.OK);
                        response.setContentType("text/plain");
                    }
                };
            }
        }));

        HttpClient client = new DefaultHttpClient();
        HttpResponse response = client.execute(new HttpGet("http://localhost:8123/hello"));

        assertEquals(200, response.getStatusLine().getStatusCode());
        assertEquals("Hello world", IOUtils.toString(response.getEntity().getContent()));
        assertEquals("text/plain", response.getEntity().getContentType().getValue());
    }

    private void on(RequestHandler requestHandler) {
        requestHandlers.add(requestHandler);
    }

    private RequestHandler get(String resourceName, Action action) {
        return new Get(new ExactResourceName(resourceName), action);
    }

}
