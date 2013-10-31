package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class Binary implements Reaction {

    private static final int READ_BUFFER_SIZE = 2048;

    private final InputStream inputStream;
    private final String contentType;

    public Binary(byte[] bytes, String contentType) {
        this(new ByteArrayInputStream(bytes), contentType);
    }

    public Binary(InputStream inputStream, String contentType) {
        this.inputStream = inputStream;
        this.contentType = contentType;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE, contentType);
        writeBytesTo(response);
    }

    private void writeBytesTo(Response response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        byte[] buffer = new byte[READ_BUFFER_SIZE];
        int bytesRead;
        while((bytesRead = inputStream.read(buffer)) > -1) {
            outputStream.write(buffer, 0, bytesRead);
        }
    }

}
