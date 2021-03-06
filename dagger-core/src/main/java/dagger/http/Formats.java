package dagger.http;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Locale;

public class Formats {

    public static DateFormat timestamp() {
        return new SimpleDateFormat("EEE, d MMM yyyy HH:mm:ss Z", Locale.US);
    }

}
