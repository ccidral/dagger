package dagger.sample.todo;

public interface Task {

    int getId();

    String getName();

    void setName(String newName);

    boolean isDone();

    void setDone(boolean done);

}
