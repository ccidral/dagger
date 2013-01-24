package dagger.module;

import dagger.*;
import org.junit.Before;
import org.junit.Test;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class ModuleBuilderTest {

    private Module mockModule;
    private Action mockAction;
    private RequestHandlerFactory mockRequestHandlerFactory;
    private ModuleBuilder moduleBuilder;

    @Before
    public void setUp() {
        mockModule = mock(Module.class);
        mockAction = mock(Action.class);
        mockRequestHandlerFactory = mock(RequestHandlerFactory.class);
        moduleBuilder = new DefaultModuleBuilder(mockModule, mockRequestHandlerFactory);
    }
    
    @Test
    public void testAddGetHandler() {
        RequestHandler get = mock(RequestHandler.class);
        when(mockRequestHandlerFactory.createGet("/foo", mockAction)).thenReturn(get);

        moduleBuilder.get("/foo", mockAction);
        
        verify(mockModule).add(get);
    }

    @Test
    public void testAddPutHandler() {
        RequestHandler put = mock(RequestHandler.class);
        when(mockRequestHandlerFactory.createPut("/foo", mockAction)).thenReturn(put);

        moduleBuilder.put("/foo", mockAction);

        verify(mockModule).add(put);
    }

    @Test
    public void testAddPostHandler() {
        RequestHandler post = mock(RequestHandler.class);
        when(mockRequestHandlerFactory.createPost("/foo", mockAction)).thenReturn(post);

        moduleBuilder.post("/foo", mockAction);

        verify(mockModule).add(post);
    }

}
