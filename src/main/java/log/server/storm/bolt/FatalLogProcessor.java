package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.analysis.ewma.FatalLogEWMA;
import log.server.storm.bolt.base.ProcessBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-23.
 */
public class FatalLogProcessor extends ProcessBolt {
    private FatalLogEWMA analyser;
    private static final long serialVersionUID = 1L;
    private int interval = 5;
    private int elapsed = 0;

    public FatalLogProcessor() {
        super(LogLevel.FATAL);

        this.analyser = new FatalLogEWMA();
    }

    public FatalLogProcessor(long interval) {
        super(LogLevel.FATAL);

        this.analyser = new FatalLogEWMA(interval);
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
