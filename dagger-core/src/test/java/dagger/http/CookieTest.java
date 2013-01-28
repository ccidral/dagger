package dagger.http;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.mock;

public class CookieTest {

    private Cookie cookie;

    @Before
    public void setUp() throws Exception {
        cookie = new CookieImpl("Greeting", "Hello");
    }

    @Test
    public void testGetName() {
        assertEquals("Greeting", cookie.getName());
    }

    @Test
    public void testGetValue() {
        assertEquals("Hello", cookie.getValue());
    }

    @Test
    public void testNoOptions() {
        assertNotNull(cookie.getOptions());
        assertEquals(0, cookie.getOptions().size());
    }

    @Test
    public void testAddOptions() {
        CookieOption option1 = mock(CookieOption.class);
        CookieOption option2 = mock(CookieOption.class);
        cookie.addOption(option1);
        cookie.addOption(option2);

        List<CookieOption> options = cookie.getOptions();
        assertEquals(2, options.size());
        assertTrue(options.contains(option1));
        assertTrue(options.contains(option2));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void testOptionListIsUnmodifiable() {
        List<CookieOption> options = cookie.getOptions();
        options.add(mock(CookieOption.class));
    }

}
