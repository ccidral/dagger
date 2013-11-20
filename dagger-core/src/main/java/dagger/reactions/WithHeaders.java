package dagger.reactions;

import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;

import java.util.HashMap;
import java.util.Map;

public class WithHeaders implements Reaction {

    private final Reaction innerReaction;
    private final Map<String, String> headers = new HashMap<String, String>();

    public WithHeaders(Reaction innerReaction) {
        this.innerReaction = innerReaction;
    }

    public WithHeaders set(String name, String value) {
        headers.put(name, value);
        return this;
    }

    @Override
    public void execute(Request request, Response response) throws Exception {
        setHeaders(response);
        innerReaction.execute(request, response);
    }

    private void setHeaders(Response response) {
        for(String name : headers.keySet())
            response.setHeader(name, headers.get(name));
    }

}
