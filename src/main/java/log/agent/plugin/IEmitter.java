package log.agent.plugin;

import log.agent.core.ILogEvent;

import java.util.List;
import java.util.Properties;

/**
 * Created by major.baek on 2015-04-07.
 */
public interface IEmitter {
    public void initialize(Properties prop);
    public void emit(ILogEvent event);
    public void emit(List<ILogEvent> batch);
}
