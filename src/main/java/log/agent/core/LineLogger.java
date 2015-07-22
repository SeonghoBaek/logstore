package log.agent.core;

import log.agent.type.LogLevel;
import log.agent.type.LogObject;
import log.agent.type.LogSchema;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-17.
 */
public class LineLogger implements ILogger {
    private LogStore logStore = null;
    private static Logger log4jLogger = null;
    private boolean useLogStoreOnly = false;
    private static LineLogger sharedLineLogger = null;

    public static LineLogger sharedInstance() {
        synchronized (LineLogger.class) {
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            if (log4jLogger == null) {
                log4jLogger = LogManager.getLogger("LogStore");
            }

            if (sharedLineLogger == null) sharedLineLogger = newInstance();
        }

        return sharedLineLogger;
    }

    public static LineLogger newInstance() {
        synchronized (LineLogger.class) {
            System.setProperty("Log4jContextSelector", "org.apache.logging.log4j.core.async.AsyncLoggerContextSelector");

            if (log4jLogger == null) {
                log4jLogger = LogManager.getLogger("LogStore");
            }
        }

        return new LineLogger(log4jLogger);
    }

    private LineLogger() {}

    public LineLogger(LogStore logStore) {
        this();
        this.logStore = logStore;
        this.useLogStoreOnly = true;
    }

    private LineLogger(Logger logger) {
       this();
       this.log4jLogger = logger;
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
        this.alarm("com", msg);
    }

    public void monitor(String msg) {
        this.monitor("com", msg);
    }

    public void sys(String msg) {
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

    public void error(String uri, Object ... events) {
        this.log(LogLevel.ERROR, uri, events);
    }

    public void debug(String uri, Object ... events) {
        this.log(LogLevel.DEBUG, uri, events);
    }

    public void info(String uri, Object ... events) {
        this.log(LogLevel.INFO, uri, events);
    }

    public void fatal(String uri, Object... events) {
        this.log(LogLevel.FATAL, uri, events);
    }

    public void warning(String uri, Object ... events) {
        this.log(LogLevel.WARNING, uri, events);
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

    public void log(int level, LogObject logObject) {
        if (logObject == null) return;

        if (this.useLogStoreOnly == true) {
            if (this.logStore != null) {
                JSONObject jsonObject = LogFormatter.formatJSON(level, logObject);
                JSONObject jsonPacket = LogFormatter.packetizeJSON(LogSchema.LINE_TYPE, 0, 0, jsonObject);

                JSONObject interceptJSONObject = LogStore.intercept(jsonPacket);

                if (interceptJSONObject != null) jsonPacket = interceptJSONObject;

                String packetLog = jsonPacket.toJSONString();

                this.logStore.produce(new BaseLogEvent(packetLog));
            }
        } else {
            if (this.log4jLogger != null) {

                //int intLevel = this.log4jLogger.getLevel().intLevel();

                int intLevel = LogStore.intLogLevel;

                if (LogLevel.intLevel(level) > intLevel) return;

                JSONObject jsonObject = LogFormatter.formatJSON(level, logObject);
                JSONObject jsonPacket = LogFormatter.packetizeJSON(LogSchema.LINE_TYPE, 0, 0, jsonObject);

                JSONObject interceptJSONObject = LogStore.intercept(jsonPacket);

                if (interceptJSONObject != null) jsonPacket = interceptJSONObject;

                String packetLog = jsonPacket.toJSONString();

                this.log4jLogger.fatal(packetLog);
            }
        }
    }

    public boolean isEnabled(int logLevel) {
        return (LogStore.intLogLevel > LogLevel.intLevel(logLevel) ? true : false);
    }

    public void log(int level, String uri, Object ... events) {
        if (events == null) return;

        if (this.useLogStoreOnly == true) {
            if (this.logStore != null) {
                JSONObject jsonObject = LogFormatter.formatJSON(level, uri, events[0]);
                JSONObject jsonPacket = LogFormatter.packetizeJSON(LogSchema.LINE_TYPE, 0, 0, jsonObject);

                JSONObject interceptJSONObject = LogStore.intercept(jsonPacket);

                if (interceptJSONObject != null) jsonPacket = interceptJSONObject;

                String packetLog = jsonPacket.toJSONString();

                this.logStore.produce(new BaseLogEvent(packetLog));
            }
        } else {
            if (this.log4jLogger != null) {
                //int intLevel = this.log4jLogger.getLevel().intLevel();

                int intLevel = LogStore.intLogLevel;

                if (LogLevel.intLevel(level) > intLevel) return;

                JSONObject jsonObject = LogFormatter.formatJSON(level, uri, events[0]);
                JSONObject jsonPacket = LogFormatter.packetizeJSON(LogSchema.LINE_TYPE, 0, 0, jsonObject);

                JSONObject interceptJSONObject = LogStore.intercept(jsonPacket);

                if (interceptJSONObject != null) jsonPacket = interceptJSONObject;

                String packetLog = jsonPacket.toJSONString();

                if (events.length > 1) {
                    Object[] newEvents = new Object[events.length - 1];

                    System.arraycopy(events, 1, newEvents, 0, newEvents.length);

                    this.log4jLogger.fatal(packetLog, newEvents);
                } else {
                    this.log4jLogger.fatal(packetLog);
                }
            }
        }
    }

    public void open() {

    }

    public void commit() {

    }
}
