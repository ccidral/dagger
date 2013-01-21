package dagger;

public interface ModuleBuilder {

    void get(String route, Action action);

    void put(String route, Action action);

}
