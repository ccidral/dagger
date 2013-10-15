package dagger.server.netty;

public class WebSocketConnectionNotFoundException extends Exception {

    public WebSocketConnectionNotFoundException(String message) {
        super(message);
    }

}
