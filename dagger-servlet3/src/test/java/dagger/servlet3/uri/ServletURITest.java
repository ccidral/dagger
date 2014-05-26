package dagger.servlet3.uri;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ServletURITest {

    @Test
    public void test_get_context_path_when_resource_path_is_absent() {
        ServletURI uri = new DefaultServletURI("/foo");
        assertEquals("/foo", uri.getContextPath());
    }

    @Test
    public void test_get_context_path_when_resource_path_is_root_path() {
        ServletURI uri = new DefaultServletURI("/bar/");
        assertEquals("/bar", uri.getContextPath());
    }

    @Test
    public void test_get_context_path_when_resource_path_is_some_regular_path() {
        ServletURI uri = new DefaultServletURI("/hello/world/again");
        assertEquals("/hello", uri.getContextPath());
    }

    @Test
    public void test_resource_path_is_null_when_absent() {
        ServletURI uri = new DefaultServletURI("/hello");
        assertNull(uri.getResourcePath());
    }

    @Test
    public void test_root_resource_path() {
        ServletURI uri = new DefaultServletURI("/hello/");
        assertEquals("/", uri.getResourcePath());
    }

    @Test
    public void test_some_regular_resource_path() {
        ServletURI uri = new DefaultServletURI("/hello/world/again");
        assertEquals("/world/again", uri.getResourcePath());
    }

}
