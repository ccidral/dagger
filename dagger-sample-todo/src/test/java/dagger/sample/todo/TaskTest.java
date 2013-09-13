package dagger.sample.todo;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.assertEquals;

public class TaskTest {

    private Task task;

    @Before
    public void setUp() throws Exception {
        task = new SimpleTask(3759, "Feed the dogs");
    }

    @Test
    public void test_id() {
        assertEquals("Task id", 3759, task.getId());
    }

    @Test
    public void test_name() {
        assertEquals("Task name", "Feed the dogs", task.getName());
    }

    @Test
    public void test_change_name() {
        task.setName("Feed the cats");
        assertEquals("New task name", "Feed the cats", task.getName());
    }

    @Test
    public void test_is_not_done_by_default() {
        assertEquals("Is task done?", false, task.isDone());
    }

    @Test
    public void test_mark_task_as_done() {
        task.setDone(true);
        assertEquals("Is task done?", true, task.isDone());
    }

}
