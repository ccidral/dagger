package dagger.reactions;

import dagger.Reaction;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.io.Files;
import dagger.lang.mime.MimeTypeGuesser;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;

public class StaticFile implements Reaction {

    private final String path;
    private final MimeTypeGuesser mimeTypeGuesser;

    public StaticFile(String path, MimeTypeGuesser mimeTypeGuesser) {
        if(!path.startsWith("/"))
            throw new IllegalArgumentException("Path must be absolute (must start with slash): " + path);

        this.path = path;
        this.mimeTypeGuesser = mimeTypeGuesser;
    }

    @Override
    public void execute(Response response) {
        URL url = getClass().getResource("/view/static" + path);

        if(url != null && Files.isFile(url)) {
            String contentType = mimeTypeGuesser.guessMimeType(url);
            response.setStatusCode(StatusCode.OK);
            response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);
            write(url, response);
        }
        else {
            response.setStatusCode(StatusCode.NOT_FOUND);
            response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain");
            write("Not found.", response);
        }
    }

    private void write(String text, Response response) {
        try {
            OutputStream outputStream = response.getOutputStream();
            try {
                outputStream.write(text.getBytes());
            } finally {
                outputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void write(URL url, Response response) {
        try {
            InputStream inputStream = url.openStream();
            try {
                OutputStream outputStream = response.getOutputStream();
                try {
                    while(true) {
                        int theByte = inputStream.read();
                        if(theByte < 0)
                            break;
                        outputStream.write(theByte);
                    }
                } finally {
                    outputStream.close();
                }
            } finally {
                inputStream.close();
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public String getPath() {
        return path;
    }

    public MimeTypeGuesser getMimeTypeGuesser() {
        return mimeTypeGuesser;
    }

}
