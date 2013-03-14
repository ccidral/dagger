package dagger;

public interface ModuleBuilder {

    void get(String route, Action action);

    void put(String route, Action action);

    void post(String route, Action action);

    /**
     * Warning: WebSocket support is experimental.
     */
    void wsopen(String route, Action action);

    /**
     * Warning: WebSocket support is experimental.
     */
    void wsmessage(String route, Action action);

    /**
     * Warning: WebSocket support is experimental.
     */
    void wsclose(String route, Action action);

}
