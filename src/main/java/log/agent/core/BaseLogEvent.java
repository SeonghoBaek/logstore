package log.agent.core;

/**
 * Created by major.baek on 2015-04-16.
 */
public class BaseLogEvent implements ILogEvent {
    protected String log = null;

    public BaseLogEvent() {}

    public BaseLogEvent(String event) {
        this.log = event;
    }

    public void set(ILogEvent event) {
        this.log = event.toString();
    }

    public String toString() {

       return this.log;
    }
}
