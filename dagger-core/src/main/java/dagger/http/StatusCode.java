package dagger.http;

public enum StatusCode {

    OK(200),
    NOT_FOUND(404);

    private final int number;

    StatusCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

}
