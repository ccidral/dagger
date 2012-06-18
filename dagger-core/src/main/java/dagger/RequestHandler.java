package dagger;

public interface RequestHandler {

    boolean canHandle(Request request);

    Result handle(Request request);

}
