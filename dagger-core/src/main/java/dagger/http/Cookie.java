package dagger.http;

import java.util.List;

public interface Cookie {

    String getName();

    String getValue();

    List<CookieOption> getOptions();

    void addOption(CookieOption option);

}
