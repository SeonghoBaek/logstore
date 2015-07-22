package test;

import log.agent.core.ConsoleLogger;
import log.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.io.InputStream;
import java.util.Collection;
import java.util.LinkedList;
import java.util.Properties;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Created by major.baek on 2015-02-16.
 */
public class TestLogEventMain {
    private Properties prop = null;
    private int producerwaittime = 20;
    private int producernumevent = 10000;
    private int producernumthread = 100;
    private boolean useLog4j2 = true;

    private static ConsoleLogger consoleLogger = ConsoleLogger.sharedInstance();

    public TestLogEventMain() {
        //org.apache.logging.log4j.core.async.AsyncLoggerContextSelector
        System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");
        loadProperties();
    }

    private void loadProperties() {
        this.prop = new Properties();
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logstore.properties");

            if (is != null) {
                this.prop.load(is);

                this.producerwaittime = Integer.parseInt(prop.getProperty("producerwaittime", "20"));
                this.producernumevent = Integer.parseInt(prop.getProperty("producernumevent", "10000"));
                this.producernumthread = Integer.parseInt(prop.getProperty("producernumthread", "100"));

                this.consoleLogger.info("test.TestLogEventMain", "Test Env - Event Count: " + this.producernumevent + ", Max Interval: " + this.producerwaittime +
                            ", Number of Producer: " + this.producernumthread);

            }
        } catch (IOException ioe) {
            this.consoleLogger.error("test.TestLogEventMain", "no properties. Use default.");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {}
        }
    }

    public void start() {
        int numThread = this.producernumthread;

        Collection<Future<?>> futures = new LinkedList<Future<?>>();
        ExecutorService executorProducer = Executors.newFixedThreadPool(numThread);

        for (int i = 0; i < numThread; i++) {
            if (this.useLog4j2 == false)
                futures.add(executorProducer.submit(new TestLogStoreProducerThread(this.producernumevent, this.producerwaittime)));
            else
                futures.add(executorProducer.submit(new TestLog4j2ProducerThread(this.producernumevent, this.producerwaittime)));
        }

        executorProducer.shutdown();

        try {
            while (futures.isEmpty() == false) {
                for (Future<?> f : futures) {
                    if (f.get() == null) {
                        futures.remove(f);
                        break;
                    } else {
                        Util.waitForSeconds(1);
                    }
                }
            }
        } catch (Exception e) {}

        System.exit(0);
    }

    public static void main(String[] args) throws Exception {
        new TestLogEventMain().start();
    }
}
