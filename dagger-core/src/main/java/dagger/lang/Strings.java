package dagger.lang;

public class Strings {

    public static boolean isNullOrBlank(String str) {
        return str == null || str.trim().length() == 0;
    }

    public static String prefixIfNecessary(char prefix, String str) {
        if(str.charAt(0) == prefix)
            return str;
        return prefix + str;
    }

    public static String emptyIfNull(String str) {
        return str == null ? "" : str;
    }

}
