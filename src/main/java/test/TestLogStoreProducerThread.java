package test;

import log.agent.core.LineLogger;
import log.agent.core.LogStore;
import log.agent.core.TransactionLogger;
import log.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 * Created by major.baek on 2015-04-06.
 */
public class TestLogStoreProducerThread implements Runnable {

    private LogStore logStore = null;
    private static int id;
    private int myId;
    private double latency;
    private long maxLatency;
    private long sentBytes = 0;

    private int eventCount = 10000;
    private int waitTime = 20;

    private Logger consoleLogger = LogManager.getLogger("Console");

    public TestLogStoreProducerThread(int eventCount, int waitTime) {
        this.myId = id++;
        this.latency = 0;
        this.maxLatency = 0;
        this.eventCount = eventCount;
        this.waitTime = waitTime;

        this.logStore = LogStore.getStore();
    }

    public void run() {
        String msg = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String evt = "";

        TransactionLogger trLogger = new TransactionLogger(this.logStore);
        LineLogger lineLogger = new LineLogger(this.logStore);

        for (int i = 0; i < 10; i++) {
            evt += msg;
        }

        long latency = 0;
        long start = System.currentTimeMillis();

        for (int i = 0; i < this.eventCount; i++) {
            latency = System.currentTimeMillis();

            sentBytes += evt.getBytes().length;

            lineLogger.info("TestLogProducerThread.run", evt);

            //trLogger.info("aaa.aa.aa", "hello world");
            //trLogger.info("aaa.aa.bb", "hello world");
            //trLogger.commit();

            latency = System.currentTimeMillis() - latency;

            this.latency += latency;

            if (this.maxLatency < latency) this.maxLatency = latency;

            int waitTime = (int)(Math.random() * this.waitTime);

            Util.waitForMillis(waitTime);
        }

        start = (System.currentTimeMillis() - start)/1000;
        consoleLogger.info("Total: " + (this.sentBytes / 1000) + " KB in " + start + "s AVG " + myId + ": " + this.latency / 1000 + ", MAX: " + this.maxLatency);
    }
}
