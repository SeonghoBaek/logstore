package test;

import log.agent.core.ConsoleLogger;
import log.agent.core.ILogger;
import log.agent.core.LineLogger;
import log.agent.core.TransactionLogger;
import log.agent.type.LogLevel;
import log.agent.type.LogObject;
import log.util.Util;

import java.io.IOException;

/**
 * Created by major.baek on 2015-04-17.
 */
public class TestLog4j2ProducerThread implements Runnable {

    private static int id;
    private int myId;
    private double latency;
    private long maxLatency;
    private long sentBytes = 0;

    private int eventCount = 10000;
    private int waitTime = 20;

    private ILogger lineLogger = LineLogger.sharedInstance();
    private ILogger transactionLogger = TransactionLogger.newInstance();;
    private ILogger consoleLogger = ConsoleLogger.sharedInstance();

    public TestLog4j2ProducerThread(int eventCount, int waitTime) {
        this.myId = id++;
        this.latency = 0;
        this.maxLatency = 0;
        this.eventCount = eventCount;
        this.waitTime = waitTime;

        //this.transactionLogger = TransactionLogger.newInstance();
        //System.out.println("TID: " + Thread.currentThread().getId());
    }

    public void run() {
        String msg = "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";
        String evt = "";

        for (int i = 0; i < 10; i++) {
            evt += msg;
        }

        long latency = 0;
        long start = System.currentTimeMillis();

        LogObject logObject = new LogObject();

        for (int i = 0; i < this.eventCount; i++) {
            latency = System.currentTimeMillis();

            sentBytes += evt.getBytes().length;

            int type = (int)((Math.random() * 100) % 10);

            switch (type) {
                case 0 :
                    transactionLogger.info("aaa.aa.info", "hello world {}", evt);
                    break;
                case 1 :
                    lineLogger.error("aaa.aa.error", evt);
                    break;
                case 2 :
                    lineLogger.warning("aaa.aa.warning", "hello world {}", evt);
                    break;
                case 3 :
                    lineLogger.debug("aaa.aa.debug", "hello world {}", evt);
                    break;
                case 5 :
                    transactionLogger.fatal("aaa.aa.fatal", "hello world {}", evt);
                    break;
                case 7 :
                    lineLogger.alarm("aaa.aa.alarm", "hello world {}", evt);
                    break;
                case 8 :
                    lineLogger.sys("aaa.aa.sys", "hello world {}", evt);
                    break;
                case 9 :
                    lineLogger.monitor("aaa.aa.monitor", "hello world {}", evt);
                    break;
                default :
                    lineLogger.info("aaa.aa.fatal", "hello world {}", evt);
                    break;
            }
/*
            if (transactionLogger.isEnabled(LogLevel.ALARM)) {
                logObject.setDeviceID("1133EFAA000-213");
                logObject.setMacAddr("01:02:22:F1:AD:09");
                logObject.setURI("com.tv.testManager");
                logObject.setEvent("test failure");
                logObject.setUserID("admin");

                lineLogger.alarm(logObject);
            }
*/
            //transactionLogger.alarm("aaa.aa.alarm", "hello world {}, {}", Thread.currentThread().getId(), evt);

            latency = System.currentTimeMillis() - latency;

            this.latency += latency;

            if (this.maxLatency < latency) this.maxLatency = latency;

            int waitTime = (int) (Math.random() * this.waitTime);

            Util.waitForMillis(waitTime);
/*
            if (lineLogger.isEnabled(LogLevel.MONITOR) == true) {
                logObject.setDeviceID("1133EFAA000-213");
                logObject.setMacAddr("01:02:22:F1:AD:09");
                logObject.setURI("com.tv.deviceid");
                logObject.setEvent("device illegal connection");
                logObject.setUserID("hacker");

                lineLogger.monitor(logObject);
            }

            IOException ioe = new IOException("IOException");
            logObject.setDeviceID("1133EFAA000-213");
            logObject.setMacAddr("01:02:22:F1:AD:09");
            logObject.setURI("com.tv.deviceid");
            logObject.setEvent(ioe);
            logObject.setUserID("hacker");

            transactionLogger.error(logObject);
*/
            transactionLogger.commit();
        }

        start = (System.currentTimeMillis() - start)/1000;

        consoleLogger.info("test.TestLog4j2ProducerThread", "ID: {}, Total: {} KB in {}s, AVG: {}, MAX: {}", myId, (this.sentBytes / 1024), start, (this.latency / this.eventCount), this.maxLatency);
    }
}
