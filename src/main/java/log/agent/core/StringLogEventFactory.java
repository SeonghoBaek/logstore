package log.agent.core;

/**
 * Created by major.baek on 2015-04-16.
 */
public class StringLogEventFactory implements ILogEventFactory {
    public ILogEvent newInstance() {
        return new StringLogEvent();
    }
}
