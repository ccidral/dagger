package resource;

import dagger.ResourceName;
import dagger.resource.ExactResourceName;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ExactResourceNameTest {

    @Test
    public void test() {
        ResourceName resourceName = new ExactResourceName("/foo");
        assertTrue(resourceName.matches("/foo"));
        assertFalse(resourceName.matches("/bar"));
    }

}
