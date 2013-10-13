package dagger.servlet3.lang;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class ServletUriParserTest {

    private ServletUriParser servletUriParser;

    @Before
    public void setUp() throws Exception {
        servletUriParser = new ServletUriParserImpl();
    }

    @Test(expected = IllegalArgumentException.class)
    public void test_null_uris_are_not_allowed() {
        servletUriParser.parse(null);
    }

    @Test
    public void test_never_return_null() {
        assertNotNull(servletUriParser.parse(""));
    }

    @Test
    public void test_context_path_is_empty_when_uri_is_empty() {
        ServletUri servletUri = servletUriParser.parse("");
        assertEquals("", servletUri.getContextPath());
    }

    @Test
    public void test_resource_path_is_empty_when_uri_is_empty() {
        ServletUri servletUri = servletUriParser.parse("");
        assertEquals("", servletUri.getResourcePath());
    }

    @Test
    public void test_context_path() {
        ServletUri servletUri = servletUriParser.parse("/contextpath");
        assertEquals("/contextpath", servletUri.getContextPath());
    }

    @Test
    public void test_resource_path() {
        ServletUri servletUri = servletUriParser.parse("/contextpath/path/to/resource");
        assertEquals("/path/to/resource", servletUri.getResourcePath());
    }

    @Test
    public void test_root_resource_path() {
        ServletUri servletUri = servletUriParser.parse("/contextpath/");
        assertEquals("/", servletUri.getResourcePath());
    }

    @Test
    public void test_resource_path_is_empty_when_missing() {
        ServletUri servletUri = servletUriParser.parse("/contextpath");
        assertEquals("", servletUri.getResourcePath());
    }

}
