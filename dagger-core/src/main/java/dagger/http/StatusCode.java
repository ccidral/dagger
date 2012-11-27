package dagger.http;

public enum StatusCode {

    OK(200),
    SEE_OTHER(303),
    NOT_MODIFIED(304),
    NOT_FOUND(404);

    private final int number;

    StatusCode(int number) {
        this.number = number;
    }

    public int getNumber() {
        return number;
    }

    public static StatusCode get(int number) {
        for(StatusCode statusCode : StatusCode.values())
            if(statusCode.getNumber() == number)
                return statusCode;

        throw new IllegalArgumentException("Unknown status code: " + number);
    }

}
