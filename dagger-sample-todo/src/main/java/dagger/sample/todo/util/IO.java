package dagger.sample.todo.util;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class IO {

    public static String asString(InputStream inputStream) throws IOException {
        StringBuilder result = new StringBuilder();
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        char[] chars = new char[64];
        int length;
        while((length = bufferedReader.read(chars)) > -1) {
            result.append(chars, 0, length);
        }
        return result.toString();
    }

}
