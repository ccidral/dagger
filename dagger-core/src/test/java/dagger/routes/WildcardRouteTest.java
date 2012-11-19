package dagger.routes;

import dagger.Route;
import org.junit.Test;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

public class WildcardRouteTest {

    @Test
    public void testMatchesSingleWildcard() {
        Route route = new WildcardRoute("/fruit/*/add");
        assertTrue(route.matches("/fruit/apple/add"));
        assertTrue(route.matches("/fruit/orange/add"));
    }

    @Test
    public void testMatchesMultipleWildcards() {
        Route route = new WildcardRoute("/car/*/part/*/add");
        assertTrue(route.matches("/car/mustang/part/wheel/add"));
    }

    @Test
    public void testDoesNotMatch() {
        Route route = new WildcardRoute("/fruit/*/add");
        assertFalse(route.matches("/fruit"));
        assertFalse(route.matches("/fruit/"));
        assertFalse(route.matches("/fruit//"));
        assertFalse(route.matches("/fruit//add"));
        assertFalse(route.matches("/car"));
        assertFalse(route.matches("/car/mustang"));
    }

}
