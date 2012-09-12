package dagger.results;

import dagger.NotImplementedYet;
import dagger.http.Response;
import dagger.http.StatusCode;

public class MockResponse implements Response {

    private StatusCode statusCode;
    private String writtenText;

    @Override
    public void setStatusCode(StatusCode statusCode) {
        this.statusCode = statusCode;
    }

    @Override
    public void write(String text) {
        this.writtenText = text;
    }

    @Override
    public void setContentType(String contentType) {
        throw new NotImplementedYet();
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public String getWrittenText() {
        return writtenText;
    }

}
