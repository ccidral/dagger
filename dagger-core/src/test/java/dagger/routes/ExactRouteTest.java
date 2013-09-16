package dagger.routes;

import dagger.Route;
import org.junit.Test;

import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertTrue;

public class ExactRouteTest {

    @Test
    public void test_does_match_exact_uri() {
        Route route = new ExactRoute("/foo");
        assertTrue(route.matches("/foo"));
    }

    @Test
    public void test_does_not_match_a_different_uri() {
        Route route = new ExactRoute("/foo");
        assertFalse(route.matches("/bar"));
    }

}
