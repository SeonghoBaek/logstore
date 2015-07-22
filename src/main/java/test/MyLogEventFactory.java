package test;

import log.agent.core.ILogEvent;
import log.agent.core.ILogEventFactory;

/**
 * Created by major.baek on 2015-04-06.
 */
public class MyLogEventFactory implements ILogEventFactory {
    public ILogEvent newInstance() {
        return new MyLogEvent();
    }
}
