package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.analysis.ewma.DebugLogEWMA;
import log.server.storm.analysis.IAnalyse;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-23.
 */
public class DebugLogProcessor extends ProcessBolt {
    private IAnalyse analyser;
    private static final long serialVersionUID = 1L;

    public DebugLogProcessor() {
        super(LogLevel.DEBUG);

        this.analyser = new DebugLogEWMA();
    }

    @Override
    public JSONObject process(JSONObject jsonLog) {
        return this.analyser.analyse(jsonLog);
    }

    public JSONObject process(String beacon) {
        return null;
    }
}
