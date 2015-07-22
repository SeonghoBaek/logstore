package log.agent.plugin.console;

import log.agent.core.ILogEvent;
import log.agent.plugin.IEmitter;

import java.util.List;
import java.util.Properties;

/**
 * Created by major.baek on 2015-04-07.
 */
public class StdOut implements IEmitter {

    public void emit(List<ILogEvent> batch) {

        if (batch.size() < 1) return;

        for (ILogEvent event:batch) {
            System.out.println(event.toString());
        }
    }

    public void initialize(Properties prop) {

    }

    public void emit(ILogEvent event) {
        System.out.println(event.toString());
    }
}
