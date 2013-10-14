package dagger.lang.io;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class Streams {

    public static String toString(InputStream inputStream) throws IOException {
        BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        while(true) {
            char[] buffer = new char[64];
            int charsRead = reader.read(buffer);
            if(charsRead < 0)
                break;
            result.append(buffer, 0, charsRead);
        }
        return result.toString();
    }

}
