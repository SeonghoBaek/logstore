package log.agent.core;

import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.WaitStrategy;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import log.agent.interceptor.IInterceptor;
import log.agent.type.LogLevel;
import log.agent.plugin.IEmitter;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-06.
 */
public class LogStore {

    private static LogStore store = null;

    private int parallelismHint = 0;
    private static boolean singleton = true;
    private Disruptor<ILogEvent> disruptor = null;
    private Executor executor;
    private ILogEventFactory factory;
    private RingBuffer<ILogEvent> ringBuffer;
    private int batchSize = 10;
    private int bufferSize = 1024;
    private String emitterClassName;
    private Properties prop;
    private String waitpolicy;

    public static int intLogLevel;
    public static boolean supportAlarmLog;
    public static boolean supportMonitorLog;
    public static boolean supportSystemLog;
    public static IInterceptor interceptor = null;

    private final String PROP_LOG_LEVEL = "loglevel";
    private final String PROP_SUPPORT_ALARM = "supportAlarmLog";
    private final String PROP_SUPPORT_MONITOR = "supportMonitorLog";
    private final String PROP_SUPPORT_SYSTEM = "supportSystemLog";
    private final String PROP_PARALLELISM = "parallelismHint";
    private final String PROP_BATCH_SIZE = "batchSize";
    private final String PROP_BUFFER_SIZE = "bufferSize";
    private final String PROP_EMITTER_NAME = "emitter.class";
    private final String PROP_INTERCEPTOR_NAME = "interceptor.class";
    private final String PROP_DOUBLE_BUFFER = "doubleBuffer";
    private final String PROP_USE_LOG4J2 = "uselog4j2";
    private final String PROP_SINGLETOM = "singleton";
    private final String PROP_WAITPOLICY = "waitpolicy";
    private final String PLUGIN_PREFIX = "log.agent.plugin.";
    private final String INTERCEPTOR_PREFIX = "log.agent.interceptor.";

    private IEmitter iEmitter = null;

    // Instance. For best performance. Do not use static variable as a l-value.
    public static LogStore newStore() {
        return new LogStore(new BaseLogEventFactory());
    }

    public static LogStore newStore(ILogEventFactory factory) {
        return new LogStore(factory);
    }

    public static LogStore newStore(ILogEventFactory factory, int bufferSize, int batchSize, int parallelismHint) {
        return new LogStore(factory, bufferSize, batchSize, parallelismHint);
    }

    // Default: BaseLogEventFactory
    public static LogStore getStore() {
        ILogEventFactory factory;

        factory = new BaseLogEventFactory();

        synchronized (LogStore.class) {
            if (singleton == true) {
                if (store == null) {
                    store = new LogStore(factory);
                }

                return store;
            }
        }

        return newStore(factory);
    }

    // Singleton. For limited resources.
    public static LogStore getStore(ILogEventFactory factory) {
        synchronized (LogStore.class) {
            if (singleton == true) {
                if (store == null) {
                    store = new LogStore(factory);
                }

                return store;
            }
        }

        return newStore(factory);
    }

    public static LogStore getStore(ILogEventFactory factory, int bufferSize, int batchSize, int parallelismHint) {
        synchronized (LogStore.class) {
            if (singleton == true) {
                if (store == null) {
                    store = new LogStore(factory, bufferSize, batchSize, parallelismHint);
                }

                return store;
            }
        }

        return newStore(factory, bufferSize, batchSize, parallelismHint);
    }

    private synchronized void loadProperties() {
        this.prop = new Properties();
        InputStream is = null;
        try {
            is = Thread.currentThread().getContextClassLoader().getResourceAsStream("logstore.properties");

            if (is != null) {
                this.prop.load(is);

                String logLevelString = prop.getProperty(PROP_LOG_LEVEL, "ALL");
                this.intLogLevel = LogLevel.intLevel(logLevelString);
                this.supportAlarmLog = Boolean.parseBoolean(prop.getProperty(PROP_SUPPORT_ALARM, "false"));
                this.supportMonitorLog = Boolean.parseBoolean(prop.getProperty(PROP_SUPPORT_MONITOR, "false"));
                this.supportSystemLog = Boolean.parseBoolean(prop.getProperty(PROP_SUPPORT_SYSTEM, "false"));

                this.parallelismHint = Integer.parseInt(prop.getProperty(PROP_PARALLELISM, "0"));
                this.batchSize = Integer.parseInt(prop.getProperty(PROP_BATCH_SIZE, "10"));
                this.bufferSize = Integer.parseInt(prop.getProperty(PROP_BUFFER_SIZE, "1024"));
                this.singleton = Boolean.parseBoolean(prop.getProperty(PROP_SINGLETOM, "false"));
                this.waitpolicy = prop.getProperty(PROP_WAITPOLICY, "SleepingWaitStrategy");

                this.emitterClassName = prop.getProperty(PROP_EMITTER_NAME, "console.StdOut");

                try {
                    //System.out.println("Create Emitter Instance");
                    this.iEmitter = (IEmitter)Class.forName(PLUGIN_PREFIX + this.emitterClassName).newInstance();
                    //System.out.println("Call Emitter Init");
                    this.iEmitter.initialize(this.prop);
                } catch (Exception e) {
                    e.printStackTrace();
                }

                String interceptorClassName = prop.getProperty(PROP_INTERCEPTOR_NAME);

                if (this.interceptor == null && interceptorClassName != null) {
                    try {
                        this.interceptor = (IInterceptor)Class.forName(INTERCEPTOR_PREFIX + interceptorClassName).newInstance();
                        this.interceptor.initialize();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (IOException ioe) {
            System.out.println("no properties. Use default.");
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
            } catch (IOException e) {}
        }
    }

    private LogStore() {}

    private LogStore(ILogEventFactory factory) {
        this.bufferSize = 1024;
        this.batchSize = 10;

        this.loadProperties();

        if (this.parallelismHint == 0) {
            this.parallelismHint = Runtime.getRuntime().availableProcessors();
        }

        System.out.println("ParallelismHint: " + this.parallelismHint + ", BatchSize: " + this.batchSize + ", BufferSize: " + this.bufferSize);

        this.executor = Executors.newCachedThreadPool();
        this.factory = factory;

        ProducerType t = ProducerType.SINGLE;

        if (this.singleton == true) {
            t = ProducerType.MULTI;
        }

        WaitStrategy ws = null;

        try {
            ws = (WaitStrategy)Class.forName("com.lmax.disruptor." + this.waitpolicy).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        this.disruptor = new Disruptor<ILogEvent>(this.factory, this.bufferSize, this.executor, t, ws);
        this.ringBuffer = this.disruptor.getRingBuffer();

        this.start();
    }

    private LogStore(ILogEventFactory factory, int bufferSize, int batchSize, int parallelismHint) {
        this.parallelismHint = parallelismHint;
        this.bufferSize = bufferSize;
        this.batchSize = batchSize;

        this.loadProperties();

        if (this.parallelismHint == 0) {
            this.parallelismHint = parallelismHint;
        }

        System.out.println("ParallelismHint: " + this.parallelismHint + ", BatchSize: " + this.batchSize + ", BufferSize: " + this.bufferSize);

        this.executor = Executors.newCachedThreadPool();
        this.factory = factory;

        ProducerType t = ProducerType.SINGLE;

        if (this.singleton == true) {
            t = ProducerType.MULTI;
        }

        WaitStrategy ws = null;

        try {
            ws = (WaitStrategy)Class.forName("com.lmax.disruptor." + this.waitpolicy).newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        //this.disruptor = new Disruptor<ILogEvent>(this.factory, this.bufferSize, this.executor, ProducerType.MULTI, new BlockingWaitStrategy());
        this.disruptor = new Disruptor<ILogEvent>(this.factory, this.bufferSize, this.executor, t, ws);
        this.ringBuffer = this.disruptor.getRingBuffer();

        this.start();
    }

    @SuppressWarnings("unchecked")
    private void start() {
        try {
            for (int threadId = 0; threadId < this.parallelismHint; threadId++) {
                EventHandler<ILogEvent> evHandler = new LogEventHandler<ILogEvent>(threadId, this.batchSize, this.parallelismHint, this.iEmitter);
                this.disruptor.handleEventsWith(evHandler);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            this.disruptor.start();
        }
    }

    public void produce(ILogEvent event) {
        long sequence = this.ringBuffer.next();

        try {
            ILogEvent target = this.ringBuffer.get(sequence);
            target.set(event);
            //System.out.println(target.toString());
        } finally {
            this.ringBuffer.publish(sequence);
        }
    }

    public RingBuffer<ILogEvent> getRingBuffer() {
        return this.ringBuffer;
    }

    public static JSONObject intercept(JSONObject jsonObject) {
        //System.out.println("intercept...");
        if (interceptor != null) {
            return interceptor.intercept(jsonObject);
        }

        return null;
    }
}
