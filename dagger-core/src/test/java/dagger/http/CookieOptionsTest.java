package dagger.http;

import org.junit.Test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class CookieOptionsTest {
    
    @Test
    public void testGetOptionsString() {
        CookieOptions options = new CookieOptionsImpl();
        assertNull(options.getOptionsString());

        options.setSecure(true);
        assertEquals("Secure", options.getOptionsString());

        options.setHttpOnly(true);
        assertEquals("Secure; HttpOnly", options.getOptionsString());

        options.setSecure(false);
        assertEquals("HttpOnly", options.getOptionsString());

        options.setHttpOnly(false);
        assertNull(options.getOptionsString());
    }

}
