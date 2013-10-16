package dagger.server.netty;

public class Millis {

    public static long seconds(int amount) {
        return amount * 1000;
    }

    public static long timeElapsedBetween(long previousTimestamp, long actualTimestamp) {
        return actualTimestamp - previousTimestamp;
    }

}
