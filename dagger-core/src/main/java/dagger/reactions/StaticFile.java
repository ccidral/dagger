package dagger.reactions;

import dagger.Reaction;
import dagger.http.Response;
import dagger.http.StatusCode;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class StaticFile implements Reaction {

    private final String path;
    private final String contentType;

    public StaticFile(String path, String contentType) {
        this.path = path;
        this.contentType = contentType;
    }

    @Override
    public void execute(Response response) {
        InputStream inputStream = getClass().getResourceAsStream("/view/static/" + path);
        OutputStream outputStream = response.getOutputStream();

        if(inputStream != null) {
            response.setStatusCode(StatusCode.OK);
            response.setContentType(contentType);
            write(inputStream, outputStream);
        }
        else {
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setContentType("text/plain");
            write("Not found.", outputStream);
        }
    }

    private void write(String text, OutputStream outputStream) {
        try {
            outputStream.write(text.getBytes());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(InputStream inputStream, OutputStream outputStream) {
        try {
            while(true) {
                int byt = inputStream.read();
                if(byt < 0)
                    break;
                outputStream.write(byt);
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

}
