package dagger;

public interface ModuleBuilder {

    void get(String uri, Action action);

}
