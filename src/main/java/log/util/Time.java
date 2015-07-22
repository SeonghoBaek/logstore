package log.util;

/**
 * Created by major.baek on 2015-04-22.
 */
public enum Time {
    MILISECOND(1),
    SECOND(1000),
    MINUTE(SECOND.getTime() * 60),
    HOUR(MINUTE.getTime() * 60),
    DAY(HOUR.getTime() * 24),
    WEEK(DAY.getTime() * 7);

    private long millis;

    private Time(long millis) {
        this.millis = millis;
    }

    public long getTime() {
        return this.millis;
    }
}
