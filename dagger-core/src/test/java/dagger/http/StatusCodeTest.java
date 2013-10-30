package dagger.http;

import org.junit.Test;

import static junit.framework.Assert.assertEquals;

public class StatusCodeTest {

    @Test
    public void testGetStatusCodeByNumber() {
        assertEquals(StatusCode.OK, StatusCode.get(StatusCode.OK.getCode()));
        assertEquals(StatusCode.NOT_FOUND, StatusCode.get(StatusCode.NOT_FOUND.getCode()));
    }

    @Test(expected = IllegalArgumentException.class)
    public void testThrowExceptionWhenTryingToGetStatusCodeWithInvalidNumber() {
        StatusCode.get(293823);
    }

}
