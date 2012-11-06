package dagger;

public interface RequestHandlerFactory {

    RequestHandler createGet(String route, Action action);

}
