package dagger.routes;

import dagger.Route;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WildcardRouteTest {

    @Test
    public void testMatchAllRoutes() {
        Route route = new WildcardRoute("/*");

        assertTrue(route.matches("/"));
        assertTrue(route.matches("/fruit"));
        assertTrue(route.matches("/fruit/orange"));
        assertTrue(route.matches("/fruit/orange/test.html"));

        assertFalse(route.matches(""));
    }

    @Test
    public void testMatchSingleWildcardInTheEnd() {
        Route route = new WildcardRoute("/fruit/*");

        assertTrue(route.matches("/fruit/"));
        assertTrue(route.matches("/fruit//"));
        assertTrue(route.matches("/fruit//add"));
        assertTrue(route.matches("/fruit/apple"));
        assertTrue(route.matches("/fruit/apple/add"));
        assertTrue(route.matches("/fruit/orange/add"));

        assertFalse(route.matches("/fruit"));
    }

    @Test
    public void testMatchesSingleWildcardInTheMiddle() {
        Route route = new WildcardRoute("/fruit/*/add");

        assertTrue(route.matches("/fruit/apple/add"));
        assertTrue(route.matches("/fruit/orange/add"));
        assertTrue(route.matches("/fruit//add"));

        assertFalse(route.matches("/fruit"));
        assertFalse(route.matches("/fruit/"));
        assertFalse(route.matches("/fruit//"));
        assertFalse(route.matches("/car"));
        assertFalse(route.matches("/car/mustang"));
    }

    @Test
    public void testMatchesMultipleWildcards() {
        Route route = new WildcardRoute("/car/*/part/*/add");
        assertTrue(route.matches("/car/mustang/part/wheel/add"));
        assertTrue(route.matches("/car/porsche/911/part/wheel/add"));

        assertFalse(route.matches("/car/corvette/wheel/add"));
    }

}
