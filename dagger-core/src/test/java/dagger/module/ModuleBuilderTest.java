package dagger.module;

import dagger.*;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ModuleBuilderTest {
    
    @Test
    public void testAddGetHandler() {
        Module mockModule = mock(Module.class);
        Action mockAction = mock(Action.class);
        RequestHandlerFactory mockRequestHandlerFactory = mock(RequestHandlerFactory.class);
        ModuleBuilder moduleBuilder = new DefaultModuleBuilder(mockModule, mockRequestHandlerFactory);

        RequestHandler get = mock(RequestHandler.class);
        when(mockRequestHandlerFactory.createGet("/foo", mockAction)).thenReturn(get);

        moduleBuilder.get("/foo", mockAction);
        
        verify(mockModule).add(get);
    }

    @Test
    public void testAddPutHandler() {
        Module mockModule = mock(Module.class);
        Action mockAction = mock(Action.class);
        RequestHandlerFactory mockRequestHandlerFactory = mock(RequestHandlerFactory.class);
        ModuleBuilder moduleBuilder = new DefaultModuleBuilder(mockModule, mockRequestHandlerFactory);

        RequestHandler put = mock(RequestHandler.class);
        when(mockRequestHandlerFactory.createPut("/foo", mockAction)).thenReturn(put);

        moduleBuilder.put("/foo", mockAction);

        verify(mockModule).add(put);
    }

}
