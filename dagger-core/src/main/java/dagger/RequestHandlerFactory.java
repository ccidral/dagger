package dagger;

public interface RequestHandlerFactory {

    RequestHandler createGet(String uri, Action action);

}
