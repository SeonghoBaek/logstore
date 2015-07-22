package log.agent.core;

import com.lmax.disruptor.EventFactory;

/**
 * Created by major.baek on 2015-02-16.
 */
public interface ILogEventFactory extends EventFactory<ILogEvent> {
    public ILogEvent newInstance();
}
