package dagger.http;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StatusCodeTest {

    @Test
    public void testGetStatusCodeByNumber() {
        assertEquals(StatusCode.OK, StatusCode.get(StatusCode.OK.getNumber()));
        assertEquals(StatusCode.NOT_FOUND, StatusCode.get(StatusCode.NOT_FOUND.getNumber()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWhenTryingToGetStatusCodeWithInvalidNumber() {
        StatusCode.get(293823);
    }

}
