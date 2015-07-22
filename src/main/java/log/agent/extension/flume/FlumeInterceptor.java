package log.agent.extension.flume;

import org.apache.flume.Event;
import org.apache.flume.interceptor.Interceptor;

import java.util.List;

/**
 * Created by major.baek on 2015-04-28.
 */
public class FlumeInterceptor implements Interceptor {
    public void initialize() {
        // Custom Interceptor
    }

    public Event intercept(Event event) {
        return null;
    }

    public List<Event> intercept(List<Event> list) {
        return null;
    }

    public void close() {

    }
}
