package dagger.sample.todo;

import org.junit.Before;
import org.junit.Test;

import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.mockito.Mockito.mock;

public class TodoListTest {

    private TodoList todoList;

    @Before
    public void setUp() throws Exception {
        todoList = new SimpleTodoList();
    }

    @Test
    public void test_adding_a_task_returns_an_instance_of_task() {
        Task task = todoList.addTask("Buy milk");
        assertEquals("Is returned task null?", false, task == null);
    }

    @Test
    public void test_task_ids_are_generated_starting_at_one() {
        Task firstTask = todoList.addTask("Feed the dogs");
        assertEquals("Task id", 1, firstTask.getId());
    }

    @Test
    public void test_generated_task_ids_are_incremented_by_one() {
        todoList.addTask("Feed the dogs");
        Task secondTask = todoList.addTask("Do the laundry");
        assertEquals("Task id", 2, secondTask.getId());
    }

    @Test
    public void test_task_name() {
        Task task = todoList.addTask("Pay bills");
        assertEquals("Task name", "Pay bills", task.getName());
    }

    @Test
    public void test_get_task_by_id() {
        todoList.addTask("Feed the cats");
        todoList.addTask("Buy bread");
        todoList.addTask("Take out the trash");

        Task retrievedTask = todoList.getTask(2);

        assertSame("Retrieved task name", "Buy bread", retrievedTask.getName());
    }

    @Test
    public void test_task_list_is_not_null() {
        List<Task> taskList = todoList.getTasks();
        assertEquals("Is task list null?", false, taskList == null);
    }

    @Test
    public void test_task_list_is_empty_when_todo_list_is_empty() {
        List<Task> taskList = todoList.getTasks();
        assertEquals("Number of tasks", 0, taskList.size());
    }

    @Test
    public void test_size_of_task_list_after_adding_two_tasks() {
        todoList.addTask("Do the laundry");
        todoList.addTask("Take out the trash");
        List<Task> taskList = todoList.getTasks();
        assertEquals("Number of tasks", 2, taskList.size());
    }

    @Test
    public void test_tasks_from_the_task_list_are_the_same_as_the_added_tasks() {
        Task task1 = todoList.addTask("Feed birds");
        Task task2 = todoList.addTask("Take a walk outside");
        List<Task> taskList = todoList.getTasks();
        assertSame(task1, taskList.get(0));
        assertSame(task2, taskList.get(1));
    }

    @Test(expected = UnsupportedOperationException.class)
    public void test_task_list_is_immutable() {
        List<Task> taskList = todoList.getTasks();
        Task task = mock(Task.class);
        taskList.add(task);
    }

}
