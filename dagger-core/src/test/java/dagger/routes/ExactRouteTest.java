package dagger.routes;

import dagger.Route;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ExactRouteTest {

    @Test
    public void test() {
        Route route = new ExactRoute("/foo");
        assertTrue(route.matches("/foo"));
        assertFalse(route.matches("/bar"));
    }

}
