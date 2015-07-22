package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.bolt.base.ExportBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-22.
 */
public class S3ExportBolt extends ExportBolt {
    private static final long serialVersionUID = 1L;

    public S3ExportBolt() {
        super(LogLevel.ALL);
    }

    public S3ExportBolt(int level) {
        super(level);
    }

    @Override
    public void export(JSONObject jsonLog) {

    }
}
