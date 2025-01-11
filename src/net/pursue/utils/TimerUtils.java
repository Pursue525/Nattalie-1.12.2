package net.pursue.utils;


import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Random;


public class TimerUtils {
    private long lastTime;
    public static long randomDelay(final int minDelay, final int maxDelay) {
        return nextInt(minDelay, maxDelay);
    }

    public void reset() {
        this.lastTime = System.currentTimeMillis();
    }

    public long getTimePassed() {
        return System.currentTimeMillis() - this.lastTime;
    }
    public boolean finished(long delay) {
        return (System.currentTimeMillis() - delay >= this.lastTime);
    }

    public boolean hasTimePassed(long time) {
        return System.currentTimeMillis() - lastTime >= time;
    }

    public static String getTime() {
        LocalDateTime currentDateTime = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        return currentDateTime.format(formatter);
    }

    public boolean reached(final long currentTime) {
        return Math.max(0L, System.currentTimeMillis() - this.lastTime) >= currentTime;
    }

    public static int nextInt(int startInclusive, int endExclusive) {
        return endExclusive - startInclusive <= 0 ? startInclusive : startInclusive + new Random().nextInt(endExclusive - startInclusive);
    }
}
