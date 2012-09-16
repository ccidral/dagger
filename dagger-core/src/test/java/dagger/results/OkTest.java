package dagger.results;

import dagger.Reaction;
import dagger.http.StatusCode;
import dagger.reactions.Ok;
import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class OkTest {

    @Test
    public void test() {
        Reaction reaction = new Ok("Some text");
        MockResponse response = new MockResponse();
        reaction.applyTo(response);

        assertEquals(StatusCode.OK, response.getStatusCode());
        assertEquals("Some text", response.getWrittenText());
    }

}
