package log.agent.core;

import log.agent.type.LogObject;

/**
 * Created by major.baek on 2015-04-17.
 */
public interface ILogger {
    public void error(String uri, Object ... events);

    public void debug(String uri, Object ... events);

    public void info(String uri, Object ... events);

    public void warning(String uri, Object ... events);

    public void fatal(String uri, Object... events);

    public void alarm(String uri, Object ... events);

    public void monitor(String uri, Object ... events);

    public void sys(String uri, Object ... events);

    public void error(String msg);

    public void fatal(String msg);

    public void debug(String msg);

    public void warning(String msg);

    public void info(String msg);

    public void alarm(String msg);

    public void monitor(String msg);

    public void sys(String msg);

    public void error(LogObject logObject);

    public void fatal(LogObject logObject);

    public void info(LogObject logObject);

    public void debug(LogObject logObject);

    public void warning(LogObject logObject);

    public void alarm(LogObject logObject);

    public void monitor(LogObject logObject);

    public void sys(LogObject logObject);

    public boolean isEnabled(int logLevel);
    public void open();
    public void commit();
}
