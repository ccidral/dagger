package dagger.servlet3;

import dagger.Module;
import dagger.ModuleFactory;

public class MockModuleFactory implements ModuleFactory {

    private static Module module;

    @Override
    public Module create() {
        return module;
    }

    public static void setModuleToBeCreated(Module module) {
        MockModuleFactory.module = module;
    }

}
