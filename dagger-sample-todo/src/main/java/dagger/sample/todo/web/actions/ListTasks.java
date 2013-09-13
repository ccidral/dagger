package dagger.sample.todo.web.actions;

import dagger.Action;
import dagger.Reaction;
import dagger.http.Request;
import dagger.reactions.Ok;
import dagger.sample.todo.Task;
import dagger.sample.todo.TodoList;

import java.util.List;

public class ListTasks implements Action {

    private final TodoList todoList;

    public ListTasks(TodoList todoList) {
        this.todoList = todoList;
    }

    @Override
    public Reaction execute(Request request) throws Exception {
        List<Task> tasks = todoList.getTasks();
        String json = toJson(tasks);
        return new Ok(json, "application/json");
    }

    private String toJson(List<Task> tasks) {
        String json = "";
        for(Task task : tasks) {
            if(json.length() > 0) json += ",";
            json += toJson(task);
        }
        return "[" + json + "]";
    }

    private String toJson(Task task) {
        return String.format("{\"id\":%d,\"name\":\"%s\"}",
            task.getId(),
            task.getName());
    }

}
