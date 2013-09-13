package dagger.sample.todo.web.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.http.Response;
import dagger.http.StatusCode;
import dagger.sample.todo.Task;
import dagger.sample.todo.TodoList;
import dagger.sample.todo.util.Mock;
import org.junit.Before;
import org.junit.Test;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

public class ListTasksActionTest {

    private Request request;
    private TodoList todoList;

    @Before
    public void setUp() throws Exception {
        request = mock(Request.class);
        todoList = mock(TodoList.class);
    }

    private void given_that_the_todo_list_contains(Task... tasks) {
        when(todoList.getTasks()).thenReturn(asList(tasks));
    }

    private Reaction executeAction() throws Exception {
        Action action = new ListTasks(todoList);
        return action.execute(request);
    }

    private Response executeActionAndReaction() throws Exception {
        Reaction reaction = executeAction();
        Response response = Mock.response();
        reaction.execute(request, response);
        return response;
    }

    @Test
    public void test_reaction_is_not_null() throws Exception {
        Reaction reaction = executeAction();
        assertEquals("Is reaction null?", false, reaction == null);
    }

    @Test
    public void test_status_code_is_ok() throws Exception {
        Response response = executeActionAndReaction();
        verify(response).setStatusCode(StatusCode.OK);
    }

    @Test
    public void test_content_type_is_json() throws Exception {
        Response response = executeActionAndReaction();
        verify(response).setHeader("Content-Type", "application/json");
    }

    @Test
    public void test_response_is_empty_array_when_todo_list_is_empty() throws Exception {
        Response response = executeActionAndReaction();
        assertEquals("Response body is an empty JSON array", "[]", Mock.contentOf(response));
    }

    @Test
    public void test_response_when_todo_list_contains_one_task() throws Exception {
        given_that_the_todo_list_contains(
            Mock.task(1, "Feed the snakes")
        );

        Response response = executeActionAndReaction();
        assertEquals("Response body is a JSON array containing one task",
            "[{\"id\":1,\"name\":\"Feed the snakes\"}]", Mock.contentOf(response));
    }

    @Test
    public void test_response_when_todo_list_contains_two_tasks() throws Exception {
        given_that_the_todo_list_contains(
            Mock.task(1, "Buy milk"),
            Mock.task(2, "Wash car")
        );

        Response response = executeActionAndReaction();
        assertEquals("Response body is a JSON array containing two tasks",
            "[{\"id\":1,\"name\":\"Buy milk\"}," +
            "{\"id\":2,\"name\":\"Wash car\"}]",
            Mock.contentOf(response));
    }

}
