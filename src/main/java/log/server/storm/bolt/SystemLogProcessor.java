package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-28.
 */
public class SystemLogProcessor extends ProcessBolt {
    public SystemLogProcessor() {
        super(LogLevel.ALL);
    }

    @Override
    public JSONObject process(JSONObject jsonLog) {
        System.out.println(jsonLog.toJSONString());
        return null;
    }

    public JSONObject process(String beacon) {
        return null;
    }
}
