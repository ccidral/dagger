package dagger.sample.todo.util;

import dagger.http.Response;
import dagger.sample.todo.Task;

import java.io.ByteArrayOutputStream;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class Mock {

    public static Task task(int id, String name) {
        Task task1 = mock(Task.class);
        when(task1.getId()).thenReturn(id);
        when(task1.getName()).thenReturn(name);
        return task1;
    }

    public static Response response() {
        Response response = mock(Response.class);
        when(response.getOutputStream()).thenReturn(new ByteArrayOutputStream());
        return response;
    }

    public static String contentOf(Response response) {
        ByteArrayOutputStream outputStream = (ByteArrayOutputStream) response.getOutputStream();
        return new String(outputStream.toByteArray());
    }

}
