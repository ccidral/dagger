package dagger.sample.todo.web.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Ok;
import dagger.sample.todo.Task;
import dagger.sample.todo.TodoList;

import static dagger.sample.todo.util.IO.asString;

public class CreateTask implements Action {
    private final TodoList todoList;

    public CreateTask(TodoList todoList) {
        this.todoList = todoList;
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        String taskName = asString(request.getBody());
        Task task = todoList.addTask(taskName);
        return new Ok(String.valueOf(task.getId()), "application/json");
    }

}
