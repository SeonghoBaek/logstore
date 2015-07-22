package log.agent.core;

/**
 * Created by major.baek on 2015-04-07.
 */
public interface ILogEvent {
    public String toString();
    public void set(ILogEvent msg);
}
