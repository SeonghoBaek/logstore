package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.analysis.IAnalyse;
import log.server.storm.analysis.ewma.WarningLogEWMA;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-23.
 */
public class WarningLogProcessor extends ProcessBolt {
    private static final long serialVersionUID = 1L;
    private IAnalyse analyser;

    public WarningLogProcessor() {
        super(LogLevel.WARNING);

        this.analyser = new WarningLogEWMA();
    }

    @Override
    public JSONObject process(JSONObject jsonLog) {
        return this.analyser.analyse(jsonLog);
    }

    public JSONObject process(String beacon) {
        return null;
    }
}
