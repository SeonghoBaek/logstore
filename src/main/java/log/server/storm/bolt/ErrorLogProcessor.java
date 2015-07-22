package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.analysis.ewma.ErrorLogEWMA;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-23.
 */
public class ErrorLogProcessor extends ProcessBolt {
    private ErrorLogEWMA analyser;
    private static final long serialVersionUID = 1L;
    private int elapsed = 0;
    private int interval = 5;

    public ErrorLogProcessor() {
        super(LogLevel.ERROR);

        this.analyser = new ErrorLogEWMA();
    }

    public JSONObject process(JSONObject jsonLog) {
        return this.analyser.analyse(jsonLog);
    }

    public JSONObject process(String beacon) {
        this.elapsed++;

        if (this.elapsed >= this.interval) {
            this.elapsed = 0;
            return this.analyser.createReport();
        }

        return null;
    }
}
