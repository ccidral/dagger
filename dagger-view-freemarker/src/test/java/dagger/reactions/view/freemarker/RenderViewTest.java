package dagger.reactions.view.freemarker;

import dagger.Reaction;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class RenderViewTest {

    @Test
    public void testWithSimpleModel() {
        String model = "World";
        Reaction reaction = new RenderView("test-with-simple-model", model);
        MockResponse response = new MockResponse();

        reaction.applyTo(response);

        assertThat(response.getOutputAsString(), equalTo("Hello World!"));
    }

}
