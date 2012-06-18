package dagger;

public interface RequestHandlers {

    void add(RequestHandler requestHandler);

    RequestHandler getHandlerFor(Request request);

}
