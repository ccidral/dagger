package dagger.resourcematchers;

import dagger.ResourceMatcher;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ExactResourceNameTest {

    @Test
    public void test() {
        ResourceMatcher resourceMatcher = new ExactResourceName("/foo");
        assertTrue(resourceMatcher.matches("/foo"));
        assertFalse(resourceMatcher.matches("/bar"));
    }

}
