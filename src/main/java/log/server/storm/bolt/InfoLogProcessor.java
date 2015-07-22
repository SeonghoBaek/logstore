package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-23.
 */
public class InfoLogProcessor extends ProcessBolt {
    private static final long serialVersionUID = 1L;

    public InfoLogProcessor() {
        super(LogLevel.INFO);
    }

    @Override
    public JSONObject process(JSONObject jsonLog) {
        return null;
    }

    public JSONObject process(String beacon) {
        return null;
    }
}
