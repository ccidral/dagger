package dagger.sample.todo;

import java.util.List;

public interface TodoList {

    Task addTask(String name);

    Task getTask(int id);

    List<Task> getTasks();

}
