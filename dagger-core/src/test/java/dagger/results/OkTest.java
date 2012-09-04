package dagger.results;

import dagger.Result;
import dagger.http.Response;
import dagger.http.StatusCode;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class OkTest {

    @Test
    public void test() {
        Result result = new Ok("Some text");
        MockResponse response = new MockResponse();
        result.applyTo(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("Some text", response.getWrittenOutput());
    }

}
