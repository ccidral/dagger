package dagger.reactions;

import dagger.Reaction;
import dagger.http.Cookie;
import dagger.http.Request;
import dagger.http.Response;

public class WithCookies implements Reaction {

    private final Reaction reaction;
    private final Cookie[] cookies;

    public WithCookies(Reaction reaction, Cookie... cookies) {
        this.reaction = reaction;
        this.cookies = cookies;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        for(Cookie cookie : cookies)
            response.setCookie(cookie);

        reaction.execute(request, response);
    }

}
