package dagger.http;

public interface Response {

    void setStatusCode(StatusCode statusCode);

    void write(String text);

    void setContentType(String contentType);

}
