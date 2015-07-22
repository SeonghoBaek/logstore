package log.server.storm.analysis.ewma;

import java.io.Serializable;

import log.util.Time;

/**
 * Created by major.baek on 2015-03-03.
 */
public class EWMA implements Serializable {

    private static final long serialVersionUID = 1L;

    public static final double ONE_MINUTE_ALPHA = 1 - Math.exp(-5d / 60d / 1d);
    public static final double FIVE_MINUTE_ALPHA = 1 - Math.exp(-5d / 60d / 5d);
    public static final double FIFTEEN_MINUTE_ALPHA = 1 - Math.exp(-5d / 60d / 15d);

    private long window = 0;
    private long alphaWindow = 0;
    private long last = 0;
    private double average = 0;
    private double alpha = -1d;
    private boolean sliding = false;

    public EWMA() {}

    public EWMA sliding(double count, Time time) {
        return this.sliding((long)(time.getTime() * count));
    }

    public EWMA sliding(long window) {
        this.sliding = true;
        this.window = window;

        return this;
    }

    public EWMA withAlpha(double alpha) {
        if (!(alpha > 0.0d && alpha <= 1.0d)) {
            throw new IllegalArgumentException("Aplpha must be between 0.0 and 1.0");
        }

        this.alpha = alpha;

        return this;
    }

    public EWMA withAlphaWindow(long alphaWindow) {
        this.alpha = -1;
        this.alphaWindow = alphaWindow;

        return this;
    }

    public EWMA withAlphaWindow(double count, Time time) {
        return this.withAlphaWindow((long)(time.getTime() * count));
    }

    public void mark() {
        mark(System.currentTimeMillis());
    }

    public void markCountsInTime(long diff, long interval) {

        long time = System.currentTimeMillis();

        if (this.sliding) {
            if (time - this.last > this.window) {
                this.average = 0;
                this.last = time;
            }
        }

        double norm;

        if (diff == 0) {
            if (this.average == 0) norm = 0;
            else norm = this.average + (1.0d + this.alpha) * this.average;
        }
        else {
            norm = (double)interval / (double)(diff);//(long)Math.sqrt( (((time - this.last)*(time - this.last)) / diff) );
        }

        double alpha = (this.alpha != -1.0d ? this.alpha : Math.exp(-1.0d * (norm / this.alphaWindow)));

        this.average = (1.0d - alpha) * norm + alpha * this.average;
        //System.out.println("norm " + norm + ", average: " + this.average + ", diff: " + diff + ", interval: " + interval);
    }

    public void markByDiff(long diff) {

        long time = System.currentTimeMillis();

        if (this.sliding) {
            if (time - this.last > this.window) {
                this.last = 0;
            }
        }

        if (this.last == 0) {
            this.average = 0;
            this.last = time;
        }

        double norm = (double)(time - this.last) / (double)diff;//(long)Math.sqrt( (((time - this.last)*(time - this.last)) / diff) );

        System.out.println("interval: " + (time - this.last) + ", norm: " + norm + ", diff: " + diff);

        double alpha = (this.alpha != -1.0d ? this.alpha : Math.exp(-1.0d * (norm / this.alphaWindow)));

        this.average = (1.0d - alpha) * norm + alpha * this.average;

        this.last = time;
    }

    public void mark(long time) {
        if (this.sliding) {
            if (time - this.last > this.window) {
                this.last = 0;
            }
        }

        if (this.last == 0) {
            this.average = 0;
            this.last = time;
        }

        long diff = time - this.last;

        if (diff < 0) { // reorder
            diff *= -1;
        } else {
            this.last = time;
        }

        //System.out.println("time: " + time + ", diff: " + diff);

        double alpha = (this.alpha != -1.0d ? this.alpha : Math.exp(-1.0d * ((double)diff / this.alphaWindow)));

        this.average = (1.0d - alpha) * diff + alpha * this.average;
    }

    public double getAverage() {
        return this.average;
    }

    public double getAverageRatePer(Time time) {
        return this.average == 0.0d ? this.average : time.getTime() / this.average;
    }
}
