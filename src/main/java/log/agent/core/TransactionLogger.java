package log.agent.core;

import log.agent.type.LogLevel;
import log.agent.type.LogObject;
import log.agent.type.LogSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * Created by major.baek on 2015-04-16.
 */
public class TransactionLogger implements ILogger {
    private ArrayList<LogElement> commitList;
    private int maxCommit = 5;

    private LogStore logStore = null;

    private boolean useLogStoreOnly = false;
    private long trStartTime = 0;
    private long trEndTime = 0;

    private class LogElement {
        public String format = null;
        public JSONObject jsonObject = null;
        public Object [] text = null;


        public LogElement(String format, Object [] text) {
            this.format = format;
            this.text = text;
        }

        public LogElement(JSONObject jsonObject, Object [] text) {
            this.text = text;
            this.jsonObject = jsonObject;
        }
    }

    private static Logger log4jLogger = null;

    public static TransactionLogger newInstance() {
        synchronized (TransactionLogger.class) {
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            if (log4jLogger == null) {
                log4jLogger = LogManager.getLogger("LogStore");
            }
        }

        return new TransactionLogger(log4jLogger);
    }

    public static TransactionLogger newInstance(int maxCommit) {
        synchronized (TransactionLogger.class) {
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            if (log4jLogger == null) {
                log4jLogger = LogManager.getLogger("LogStore");
            }
        }

        return new TransactionLogger(log4jLogger, maxCommit);
    }

    private TransactionLogger() {
        commitList = new ArrayList<LogElement>();
    }

    private TransactionLogger(Logger logger) {
        this();

        this.log4jLogger = logger;

        this.open();
    }

    private TransactionLogger(Logger logger, int maxCommit) {
        this();

        this.maxCommit = maxCommit;
        this.log4jLogger = logger;
        if (this.maxCommit < 1) this.maxCommit = 1;

        this.open();
    }

    public TransactionLogger(LogStore logStore) {
        this();

        this.logStore = logStore;
        this.useLogStoreOnly = true;
        this.open();
    }

    public TransactionLogger(LogStore logStore, int maxCommit) {
        this();

        this.maxCommit = maxCommit;
        this.logStore = logStore;
        this.useLogStoreOnly = true;
        this.open();
    }

    public void open() {
        this.trEndTime = this.trStartTime = 0;
    }

    public void error(String msg) {
        this.error("com", msg);
    }

    public void fatal(String msg) {
        this.fatal("com", msg);
    }

    public void debug(String msg) {
        this.debug("com", msg);
    }

    public void warning(String msg) {
        this.warning("com", msg);
    }

    public void info(String msg) {
        this.info("com", msg);
    }

    public void alarm(String msg) {
        if (LogStore.supportAlarmLog == false) return;
        this.alarm("com", msg);
    }

    public void monitor(String msg) {
        if (LogStore.supportMonitorLog == false) return;
        this.monitor("com", msg);
    }

    public void sys(String msg) {
        if (LogStore.supportSystemLog == false) return;
        this.sys("com", msg);
    }

    public void error(LogObject logObject) {
        this.log(LogLevel.ERROR, logObject);
    }

    public void fatal(LogObject logObject) {
        this.log(LogLevel.FATAL, logObject);
    }

    public void info(LogObject logObject) {
        this.log(LogLevel.INFO, logObject);
    }

    public void debug(LogObject logObject) {
        this.log(LogLevel.DEBUG, logObject);
    }

    public void warning(LogObject logObject) {
        this.log(LogLevel.WARNING, logObject);
    }

    public void alarm(LogObject logObject) {
        if (LogStore.supportAlarmLog == false) return;
        this.log(LogLevel.ALARM, logObject);
    }

    public void monitor(LogObject logObject) {
        if (LogStore.supportMonitorLog == false) return;
        this.log(LogLevel.MONITOR, logObject);
    }

    public void sys(LogObject logObject) {
        if (LogStore.supportSystemLog == false) return;
        this.log(LogLevel.SYSTEM, logObject);
    }

    public void error(String uri, Object... events) {
         this.log(LogLevel.ERROR, uri, events);
    }

    public void debug(String uri, Object... events) {
         this.log(LogLevel.DEBUG, uri, events);
    }

    public void warning(String uri, Object... events) {
         this.log(LogLevel.WARNING, uri, events);
    }

    public void fatal(String uri, Object... events) {
         this.log(LogLevel.FATAL, uri, events);
    }

    public void info(String uri, Object ... events) {
         this.log(LogLevel.INFO, uri, events);
    }

    public void alarm(String uri, Object ... events) {
        if (LogStore.supportAlarmLog == false) return;
        this.log(LogLevel.ALARM, uri, events);
    }

    public void monitor(String uri, Object ... events) {
        if (LogStore.supportMonitorLog == false) return;
        this.log(LogLevel.MONITOR, uri, events);
    }

    public void sys(String uri, Object ... events) {
        if (LogStore.supportSystemLog == false) return;
        this.log(LogLevel.SYSTEM, uri, events);
    }

    public void commit() {
        if (this.commitList.size() < 1) return;

        String format = "";
        ArrayList<Object> textLists = new ArrayList<Object>();

        StringBuilder sb = new StringBuilder();

        this.trEndTime = System.currentTimeMillis();

        JSONArray jsonArray = new JSONArray();

        for(LogElement log : this.commitList) {

            jsonArray.add(log.jsonObject);

            if (log.text != null) {
                textLists.addAll(Arrays.asList(log.text));
            }
        }

        JSONObject packetJSON = LogFormatter.packetizeJSON(LogSchema.TRANSACTION_TYPE, this.trStartTime, this.trEndTime, jsonArray);

        JSONObject interceptJSONObject = LogStore.intercept(packetJSON);

        if (interceptJSONObject != null) packetJSON = interceptJSONObject;

        if (this.useLogStoreOnly == true) {
            if (this.logStore != null) {
                this.logStore.produce(new BaseLogEvent(packetJSON.toJSONString()));
            }
        } else {
            if (this.log4jLogger != null) {
                if (textLists.size() > 0)
                    this.log4jLogger.fatal(packetJSON.toJSONString(), textLists.toArray());
                else
                    this.log4jLogger.fatal(packetJSON.toJSONString());
            }
        }

        commitList.clear();
        this.open();
    }

    public void log(int level, LogObject logObject) {
        if (logObject == null) return;

        int intLevel = LogStore.intLogLevel;

        if (intLevel < LogLevel.intLevel(level)) return;

        if (this.useLogStoreOnly == false) {
            if (this.log4jLogger == null) return;

            //if (this.log4jLogger.getLevel().intLevel() < LogLevel.intLevel(level)) return;
        }

        if (this.trStartTime == 0) this.trStartTime = System.currentTimeMillis();

        JSONObject jsonObject = LogFormatter.formatJSON(level, logObject);
        commitList.add(new LogElement(jsonObject, null));

        if (commitList.size() >= this.maxCommit) {
            this.commit();
        }
    }

    public void log(int level, String uri, Object ... events) {
        if (events == null) return;

        int intLevel = LogStore.intLogLevel;

        if (intLevel < LogLevel.intLevel(level)) return;

        if (this.useLogStoreOnly == false) {
            if (this.log4jLogger == null) return;

            //if (intLevel < LogLevel.intLevel(level)) return;
        }

        if (this.trStartTime == 0) this.trStartTime = System.currentTimeMillis();

        JSONObject jsonObject = LogFormatter.formatJSON(level, uri, events[0]);

        if (this.useLogStoreOnly == false) {

            if (events.length > 1) {

                Object[] newEvents = new Object[events.length - 1];

                System.arraycopy(events, 1, newEvents, 0, newEvents.length);

                LogElement elem = new LogElement(jsonObject, newEvents);

                commitList.add(elem);
            } else {
                commitList.add(new LogElement(jsonObject, null));
            }
        } else {
            commitList.add(new LogElement(jsonObject, null));
        }

        if (commitList.size() >= this.maxCommit) {
            this.commit();
        }
    }

    public boolean isEnabled(int logLevel) {
        return (LogStore.intLogLevel > LogLevel.intLevel(logLevel) ? true : false);
    }
}
