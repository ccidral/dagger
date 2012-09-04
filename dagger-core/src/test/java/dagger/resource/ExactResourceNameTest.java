package dagger.resource;

import dagger.ResourcePattern;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ExactResourceNameTest {

    @Test
    public void test() {
        ResourcePattern resourcePattern = new ExactResourceName("/foo");
        assertTrue(resourcePattern.matches("/foo"));
        assertFalse(resourcePattern.matches("/bar"));
    }

}
