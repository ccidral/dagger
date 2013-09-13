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

import java.io.ByteArrayInputStream;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class CreateTaskActionTest {

    private TodoList todoList;
    private Action action;
    private Request request;
    private Response response;

    @Before
    public void setUp() throws Exception {
        todoList = mock(TodoList.class);
        request = mock(Request.class);
        response = Mock.response();
        action = new CreateTask(todoList);
    }

    private void given_that_the_request_contains_a_body(String body) {
        when(request.getBody()).thenReturn(new ByteArrayInputStream(body.getBytes()));
    }

    private void given_that_a_task_is_created_when_added_to_the_todo_list(int id, String name) {
        Task newTask = Mock.task(id, name);
        when(todoList.addTask(name)).thenReturn(newTask);
    }

    @Test
    public void test_reaction_is_not_null() throws Exception {
        given_that_the_request_contains_a_body("Call mom");
        given_that_a_task_is_created_when_added_to_the_todo_list(1, "Call mom");

        Reaction reaction = action.execute(request);

        assertEquals("Is reaction null?", false, reaction == null);
    }

    @Test
    public void test_status_code_is_ok() throws Exception {
        given_that_the_request_contains_a_body("Buy bread");
        given_that_a_task_is_created_when_added_to_the_todo_list(2, "Buy bread");

        Reaction reaction = action.execute(request);
        reaction.execute(request, response);

        verify(response).setStatusCode(StatusCode.OK);
    }

    @Test
    public void test_response_content_type_is_application_json() throws Exception {
        given_that_the_request_contains_a_body("Take a walk outside");
        given_that_a_task_is_created_when_added_to_the_todo_list(3, "Take a walk outside");

        Reaction reaction = action.execute(request);
        reaction.execute(request, response);

        verify(response).setHeader("Content-Type", "application/json");
    }

    @Test
    public void test_add_task_to_the_todo_list() throws Exception {
        given_that_the_request_contains_a_body("Feed the parrot");
        given_that_a_task_is_created_when_added_to_the_todo_list(4, "Feed the parrot");

        Reaction reaction = action.execute(request);
        reaction.execute(request, response);

        verify(todoList).addTask("Feed the parrot");
    }

    @Test
    public void test_response_body_contains_the_new_task_id() throws Exception {
        given_that_the_request_contains_a_body("Take a walk outside");
        given_that_a_task_is_created_when_added_to_the_todo_list(8264, "Take a walk outside");

        Reaction reaction = action.execute(request);
        reaction.execute(request, response);

        assertEquals("Response body", "8264", Mock.contentOf(response));
    }

}
