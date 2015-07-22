package log.agent.extension.flume;

import log.agent.core.ConsoleLogger;
import log.agent.core.LineLogger;
import log.agent.core.LogStore;
import log.agent.core.TransactionLogger;
import org.apache.flume.*;
import org.apache.flume.conf.Configurable;
import org.apache.flume.sink.AbstractSink;

/**
 * Created by major.baek on 2015-04-08.
 */
public class FlumeSink extends AbstractSink implements Configurable {
    private LogStore store = null;
    private LineLogger lineLogger = null;
    private TransactionLogger trLogger = null;

    private final String URI_PREFIX = "sys.flume.";
    private final String INTERCEPTOR_KEY = "source";

    public void configure(Context context) {
        this.store = LogStore.newStore();
        this.lineLogger = new LineLogger(this.store);
        this.trLogger = new TransactionLogger(this.store);

        System.out.println("FlumeSink configured");
    }

    public void start() {
        System.out.println("FlumeSink started");
    }

    public void stop() {
        System.out.println("FlumeSink stopped");
    }

    public Status process() throws EventDeliveryException {
        Status status = null;
        Channel ch = getChannel();
        Transaction txn = ch.getTransaction();
        txn.begin();

        try {
            Event event = ch.take();

            String source = event.getHeaders().get(INTERCEPTOR_KEY);
            String log = new String(event.getBody());
            String uri = URI_PREFIX + source;

            this.lineLogger.sys(uri, log);

            txn.commit();
            status = Status.READY;
        } catch (Throwable t) {
            txn.rollback();
            status = Status.BACKOFF;

            if (t instanceof Error) {
                throw (Error)t;
            }
        } finally {
            txn.close();
        }

        return status;
    }
}
