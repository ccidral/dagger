package dagger.sample.todo;

import java.util.ArrayList;
import java.util.List;

import static java.util.Collections.unmodifiableList;

public class SimpleTodoList implements TodoList {

    private final List<Task> tasks = new ArrayList<>();
    private int lastGeneratedId;

    @Override
    public Task addTask(String name) {
        int id = ++lastGeneratedId;
        Task task = new SimpleTask(id, name);
        tasks.add(task);
        return task;
    }

    @Override
    public Task getTask(int id) {
        for(Task task : tasks)
            if(id == task.getId())
                return task;
        return null;
    }

    @Override
    public List<Task> getTasks() {
        return unmodifiableList(tasks);
    }

}
