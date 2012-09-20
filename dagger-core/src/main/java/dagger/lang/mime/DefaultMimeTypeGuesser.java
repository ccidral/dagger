package dagger.lang.mime;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class DefaultMimeTypeGuesser implements MimeTypeGuesser {

    private Map<String, String> map = new HashMap<String, String>();

    public DefaultMimeTypeGuesser() {
        loadMimeTypesFromResourceFile();
    }

    private void loadMimeTypesFromResourceFile() {
        InputStream mimeTypesFile = getClass().getResourceAsStream("/mime.types");
        BufferedReader reader = new BufferedReader(new InputStreamReader(mimeTypesFile));
        try {
            loadMimeTypesFrom(reader);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private void loadMimeTypesFrom(BufferedReader reader) throws IOException {
        Pattern pattern = Pattern.compile("^([^\\s]+)\\s+([a-z0-9 ]+)$");
        while(true) {
            String line = reader.readLine();
            if(line == null)
                break;

            if(isLineCommented(line))
                continue;

            Matcher matcher = pattern.matcher(line);
            if(matcher.matches())
                addParsedMimeType(matcher);
        }
    }

    private void addParsedMimeType(Matcher matcher) {
        String mimeType = matcher.group(1);
        String fileExtensionsSeparatedByComma = matcher.group(2);
        addMimeTypeForExtensions(mimeType, fileExtensionsSeparatedByComma);
    }

    private boolean isLineCommented(String line) {
        return line.startsWith("#");
    }

    private void addMimeTypeForExtensions(String mimeType, String fileExtensionsSeparatedByComma) {
        String[] fileExtensions = fileExtensionsSeparatedByComma.split(" ");
        for(String fileExtension : fileExtensions)
            map.put(fileExtension, mimeType);
    }

    @Override
    public String guessMimeType(URL url) {
        String fileExtension = getFileExtension(url);
        if(fileExtension != null && map.containsKey(fileExtension))
            return map.get(fileExtension);

        return "application/octet-stream";
    }

    private String getFileExtension(URL url) {
        String fileName = url.getFile();
        int lastDotIndex = fileName.lastIndexOf(".");
        if(lastDotIndex >= 0)
            return fileName.substring(lastDotIndex + 1);

        return null;
    }

}
