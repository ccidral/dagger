package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeader;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.*;

public class Text implements Reaction {

    private static final int READ_BUFFER_SIZE = 2048;

    private final Reader reader;
    private final String contentType;

    public Text(String text, String contentType) {
        this(new StringReader(text), contentType);
    }

    public Text(InputStream inputStream, String contentType) {
        this(new InputStreamReader(inputStream), contentType);
    }

    public Text(Reader reader, String contentType) {
        this.reader = reader;
        this.contentType = contentType;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeader.CONTENT_TYPE, contentType);
        writeTextTo(response);
    }

    private void writeTextTo(Response response) throws IOException {
        Writer writer = new OutputStreamWriter(response.getOutputStream());
        char[] buffer = new char[READ_BUFFER_SIZE];
        int charsRead;
        while((charsRead = reader.read(buffer)) > -1) {
            writer.write(buffer, 0, charsRead);
        }
        writer.flush();
    }

}
