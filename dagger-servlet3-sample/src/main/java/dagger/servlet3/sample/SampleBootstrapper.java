package dagger.servlet3.sample;

import dagger.Action;
import dagger.RequestHandlers;
import dagger.Result;
import dagger.handlers.Get;
import dagger.resourcematchers.ExactResourceName;
import dagger.results.Ok;
import dagger.servlet3.Bootstrapper;

public class SampleBootstrapper implements Bootstrapper {

    @Override
    public void bootstrap(RequestHandlers requestHandlers) {
         requestHandlers.add(new Get(new ExactResourceName("/"), new Action() {
             @Override
             public Result execute() {
                 return new Ok("Home");
             }
         }));
    }

}
