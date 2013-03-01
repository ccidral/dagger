package dagger.reactions.view.freemarker;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RenderViewTest {

    @Test
    public void testWithSimpleModel() throws Exception {
        String model = "World";
        String contentType = "foo/bar";

        Reaction reaction = new RenderView("test-with-simple-model", contentType, model);
        MockResponse response = new MockResponse();

        reaction.execute(null, response);

        assertThat(response.getOutputAsString(), equalTo("Hello World!"));
        assertThat(response.getHeader(HttpHeaderNames.CONTENT_TYPE), equalTo(contentType));
    }

}
