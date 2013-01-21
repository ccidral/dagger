package dagger;

public interface RequestHandlerFactory {

    RequestHandler createGet(String route, Action action);

    RequestHandler createPut(String route, Action action);

}
