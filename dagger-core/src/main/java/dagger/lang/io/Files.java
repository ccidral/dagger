package dagger.lang.io;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Files {

    public static boolean isFile(URL url) {
        assertNotNull(url);

        if(isWithinJar(url)) {
            return isFileWithinJar(url);

        } else {
            return isRegularFile(url);
        }
    }

    private static boolean isRegularFile(URL url) {
        return toFile(url).isFile();
    }

    private static File toFile(URL url) {
        File file;
        try {
            file = new File(url.toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }
        return file;
    }

    private static boolean isFileWithinJar(URL url) {
        Matcher matcher = jarFileMatcher(url);

        if(!matcher.matches())
            return false;

        String jarPath = matcher.group(1);
        String filePath = matcher.group(2);

        try {
            JarFile jarFile = new JarFile(jarPath);
            JarEntry jarEntry = jarFile.getJarEntry(filePath.substring(1));
            return !jarEntry.isDirectory();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private static Matcher jarFileMatcher(URL url) {
        Pattern pattern = Pattern.compile("jar:file:([^!]+\\.jar)!(.*)");
        return pattern.matcher(url.toString());
    }

    private static boolean isWithinJar(URL url) {
        Matcher matcher = jarFileMatcher(url);
        return matcher.matches();
    }

    private static void assertNotNull(URL url) {
        if(url == null)
            throw new IllegalArgumentException("Parameter 'url' must not be null");
    }

}
