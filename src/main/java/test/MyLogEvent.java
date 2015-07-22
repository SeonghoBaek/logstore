package test;

import log.agent.core.ILogEvent;

/**
 * Created by major.baek on 2015-04-06.
 */
public class MyLogEvent implements ILogEvent {

    private String message;

    public MyLogEvent() {
        message = null;
    }

    public MyLogEvent(String message) {
        this.message = message;
    }

    public String toJSON() {
        return message;
    }

    public void set(ILogEvent event) {
        this.message = event.toString();
    }

    public String toString() {
        return message;
    }
}
