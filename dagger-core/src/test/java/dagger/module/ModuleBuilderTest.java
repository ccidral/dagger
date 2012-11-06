package dagger.module;

import dagger.*;
import dagger.module.DefaultModuleBuilder;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ModuleBuilderTest {
    
    @Test
    public void testAddGetHandler() {
        Module mockModule = mock(Module.class);
        RequestHandler get = mock(RequestHandler.class);
        Action mockAction = mock(Action.class);
        RequestHandlerFactory mockRequestHandlerFactory = mock(RequestHandlerFactory.class);

        when(mockRequestHandlerFactory.createGet("/foo", mockAction)).thenReturn(get);

        ModuleBuilder moduleBuilder = new DefaultModuleBuilder(mockModule, mockRequestHandlerFactory);

        moduleBuilder.get("/foo", mockAction);
        
        verify(mockModule).add(get);
    }
    
}
