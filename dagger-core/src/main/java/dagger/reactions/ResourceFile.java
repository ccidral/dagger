package dagger.reactions;

import dagger.Reaction;
import dagger.http.*;
import dagger.lang.io.Files;
import dagger.lang.mime.MimeTypeGuesser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URISyntaxException;
import java.net.URL;
import java.text.ParseException;
import java.util.Date;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import static dagger.http.HttpHeaderNames.IF_MODIFIED_SINCE;

public class ResourceFile implements Reaction {

    private final String path;
    private final MimeTypeGuesser mimeTypeGuesser;
    private final Logger logger;

    public ResourceFile(String path, MimeTypeGuesser mimeTypeGuesser) {
        if(!path.startsWith("/"))
            throw new IllegalArgumentException("Path must be absolute (must start with slash): " + path);

        this.path = path;
        this.mimeTypeGuesser = mimeTypeGuesser;
        this.logger = LoggerFactory.getLogger(getClass().getName()+"["+path+"]");
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        URL url = getClass().getResource("/view/static" + path);
        if(url == null || !Files.isFile(url))
            writeNotFound(response);
        else {
            Date lastModificationDate = parseDate(request.getHeader(IF_MODIFIED_SINCE));
            writeFileIfModifiedSince(lastModificationDate, url, response);
        }
    }

    private void writeFileIfModifiedSince(Date lastModificationDate, URL url, Response response) throws URISyntaxException, IOException {
        Date modificationDate = getFileModificationDate(url);
        if(lastModificationDate == null || modificationDate.after(lastModificationDate))
            writeFileTo(response, url);
        else
            writeNotModified(response);
    }

    private void writeNotModified(Response response) throws IOException {
        response.setStatusCode(StatusCode.NOT_MODIFIED);
        write("Not modified", response);
    }

    private Date parseDate(String text) throws ParseException {
        if(text != null) {
            try {
                return Formats.timestamp().parse(text);
            }
            catch(Exception e) {
                logger.error("Failed to parse date " + text, e);
            }
        }
        return null;
    }

    private void writeNotFound(Response response) throws IOException {
        response.setStatusCode(StatusCode.NOT_FOUND);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, "text/plain");
        write("Not found.", response);
    }

    private void writeFileTo(Response response, URL fileUrl) throws URISyntaxException, IOException {
        String contentType = mimeTypeGuesser.guessMimeType(fileUrl);
        Date modificationDate = getFileModificationDate(fileUrl);
        response.setStatusCode(StatusCode.OK);
        response.setHeader(HttpHeaderNames.CONTENT_TYPE, contentType);
        response.setHeader(HttpHeaderNames.LAST_MODIFIED, Formats.timestamp().format(modificationDate));
        write(fileUrl, response);
    }

    private Date getFileModificationDate(URL fileUrl) throws URISyntaxException, IOException {
        ZipEntry fileInsideJar = tryReadingFileFromInsideJar(fileUrl);
        if(fileInsideJar != null)
            return new Date(fileInsideJar.getTime());

        File file = new File(fileUrl.toURI());
        return new Date(file.lastModified());
    }

    private ZipEntry tryReadingFileFromInsideJar(URL fileUrl) throws IOException {
        Pattern pattern = Pattern.compile("^jar:file:([^!]*)!(.*)$");
        Matcher matcher = pattern.matcher(fileUrl.toString());

        if(!matcher.matches())
            return null;

        String jarPath = matcher.group(1);
        String filePath = matcher.group(2);
        ZipFile jar = new ZipFile(jarPath);
        return jar.getEntry(removeTrailingSlashFrom(filePath));
    }

    private void write(String text, Response response) throws IOException {
        OutputStream outputStream = response.getOutputStream();
        try {
            outputStream.write(text.getBytes());
        } finally {
            outputStream.close();
        }
    }

    private void write(URL url, Response response) throws IOException {
        InputStream inputStream = url.openStream();
        try {
            OutputStream outputStream = response.getOutputStream();
            try {
                copy(inputStream, outputStream);
            } finally {
                outputStream.close();
            }
        } finally {
            inputStream.close();
        }
    }

    private void copy(InputStream source, OutputStream target) throws IOException {
        while(true) {
            int theByte = source.read();
            if(theByte < 0)
                break;
            target.write(theByte);
        }
    }

    public String getPath() {
        return path;
    }

    public MimeTypeGuesser getMimeTypeGuesser() {
        return mimeTypeGuesser;
    }

    private String removeTrailingSlashFrom(String filePath) {
        return filePath.substring(1);
    }

}
