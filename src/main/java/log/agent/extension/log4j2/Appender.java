package log.agent.extension.log4j2;

import log.agent.core.BaseLogEvent;
import log.agent.core.LogStore;
import org.apache.logging.log4j.core.Layout;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.appender.AbstractAppender;
import org.apache.logging.log4j.core.config.plugins.Plugin;
import org.apache.logging.log4j.core.config.plugins.PluginAttribute;
import org.apache.logging.log4j.core.config.plugins.PluginElement;
import org.apache.logging.log4j.core.config.plugins.PluginFactory;

import java.io.Serializable;

/**
 * Created by major.baek on 2015-04-09.
 */
@Plugin(name = "LogStore", category = "Core", elementType = "log4j2", printObject = true)
public class Appender extends AbstractAppender {

    private final int NUM_STORE = 2;
    private LogStore [] logstoreArray;
    private static int selector = 0;

    private Appender(final String name, Layout<? extends Serializable> layout) {
        super(name, null, layout, true);

        logstoreArray = new LogStore[NUM_STORE];

        for (int i = 0; i < NUM_STORE; i++) {
            logstoreArray[i] = LogStore.newStore();
        }
    }

    public void append(LogEvent event) {
        selector = ~selector & 1;

        LogStore logStore = logstoreArray[selector];

        Layout l = this.getLayout();
        String msg;

        if (l == null) {
            msg = event.getMessage().getFormattedMessage();
        } else {
            msg = (String) (l.toSerializable(event));
        }

        logStore.produce(new BaseLogEvent(msg));
    }

    @PluginFactory
    public static Appender createAppender(@PluginAttribute("name") final String name,
                                          @PluginElement("Layout") Layout<? extends Serializable> layout) {
        Layout<? extends Serializable> l = layout;

        if (name == null) {
            LOGGER.error("No name provided for AsyncAppender");
            return null;
        }

        /*
        if(layout == null) {
            l = PatternLayout.createDefaultLayout();
        }
        */

        return new Appender(name, l);
    }
}
