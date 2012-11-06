package dagger.server.netty;

import org.junit.Test;

import java.util.Map;

import static junit.framework.Assert.*;

public class QueryStringTest {

    @Test
    public void testNoQueryParametersFromUri() {
        QueryString params = QueryString.fromUri("/hello/world");

        assertNotNull("QueryString is not null", params);
        assertEquals("Has zero parameters", 0, params.size());
    }

    @Test
    public void testQueryParametersFromUri() {
        QueryString params = QueryString.fromUri("/hello/world?fruit=apple&car=mustang");

        assertNotNull("Query parameters is not null", params);
        assertEquals("Two query parameters", 2, params.size());
        assertEquals("apple", params.get("fruit"));
        assertEquals("mustang", params.get("car"));
    }

    @Test
    public void testDiscardParameterWithEmptyName() {
        QueryString params = QueryString.fromUri("/hello/world?=apple&car=fiesta");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There is only one query parameter", 1, params.size());
        assertEquals("fiesta", params.get("car"));
    }

    @Test
    public void testDiscardParameterWithWhitespaceOnlyName() {
        QueryString params = QueryString.fromUri("/hello/world?   =apple&car=fiesta");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There is only one query parameter", 1, params.size());
        assertEquals("fiesta", params.get("car"));
    }

    @Test
    public void testDiscardWhitespaceOnlyParameter() {
        QueryString params = QueryString.fromUri("/hello/world?   &car=fiesta");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There is only one query parameter", 1, params.size());
        assertEquals("fiesta", params.get("car"));
    }

    @Test
    public void testParameterWithEmptyValueIsEmptyString() {
        QueryString params = QueryString.fromUri("/hello/world?fruit=&car=mustang");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There are two query parameters", 2, params.size());
        assertEquals("Empty value is empty string", "", params.get("fruit"));
        assertEquals("mustang", params.get("car"));
    }

    @Test
    public void testParameterWithNoValueIsNull() {
        QueryString params = QueryString.fromUri("/hello/world?fruit&car=ferrari");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There are two query parameters", 2, params.size());
        assertNull("Value is null when value is not present", params.get("fruit"));
        assertEquals("ferrari", params.get("car"));
    }

    @Test
    public void testParameterWithWhitespaceOnlyValue() {
        QueryString params = QueryString.fromUri("/hello/world?fruit=   &car=bmw");

        assertNotNull("Query parameters is not null", params);
        assertEquals("There are two query parameters", 2, params.size());
        assertEquals("   ", params.get("fruit"));
        assertEquals("bmw", params.get("car"));
    }

    @Test
    public void testMap() {
        QueryString params = QueryString.fromUri("/hello/world?fruit=apple&car=mustang");
        Map<String,String> map = params.map();

        assertNotNull("Returned map is not null", map);
        assertEquals("Map has two query parameters", 2, map.size());
        assertEquals("apple", map.get("fruit"));
        assertEquals("mustang", map.get("car"));
    }

    @Test
    public void testNeverReturnSameMapInstance() {
        QueryString params = QueryString.fromUri("/hello/world?fruit=apple&car=mustang");
        assertNotSame(params.map(), params.map());
    }

}