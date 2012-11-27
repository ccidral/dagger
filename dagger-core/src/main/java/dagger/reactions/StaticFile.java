package dagger.reactions;

import dagger.Reaction;
import dagger.http.Formats;
import dagger.http.HttpHeaderNames;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.lang.io.Files;
import dagger.lang.mime.MimeTypeGuesser;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

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
        if(url == null || !Files.isFile(url))
            writeNotFound(response);
        else
            writeFileTo(response, url);
    }

    private void writeNotFound(Response response) {
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        write("Not found.", response);
    }

    private void writeFileTo(Response response, URL fileUrl) {
        String contentType = mimeTypeGuesser.guessMimeType(fileUrl);
        Date modificationDate = getFileModificationDate(fileUrl);
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.setHeader(HttpHeaderNames.LAST_MODIFIED, Formats.TIMESTAMP.format(modificationDate));
        write(fileUrl, response);
    }

    private Date getFileModificationDate(URL fileUrl) {
        ZipEntry fileInsideJar = tryReadingFileFromInsideJar(fileUrl);
        if(fileInsideJar != null) {
            return new Date(fileInsideJar.getTime());
        }
        else {
            try {
                File file = new File(fileUrl.toURI());
                return new Date(file.lastModified());
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            }
        }
    }

    private ZipEntry tryReadingFileFromInsideJar(URL fileUrl) {
        Pattern pattern = Pattern.compile("^jar:file:([^!]*)!(.*)$");
        Matcher matcher = pattern.matcher(fileUrl.toString());

        if(matcher.matches()) {
            String jarPath = matcher.group(1);
            String filePath = matcher.group(2);
            try {
                ZipFile jar = new ZipFile(jarPath);
                return jar.getEntry(removeTrailingSlashFrom(filePath));
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return null;
    }

    private String removeTrailingSlashFrom(String filePath) {
        return filePath.substring(1);
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
