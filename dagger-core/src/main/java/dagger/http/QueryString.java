package dagger.http;

import java.util.Map;

public interface QueryString {

    int size();

    String get(String name);

    Map<String,String> map();

}
