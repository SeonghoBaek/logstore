package log.server.storm.bolt;

import log.agent.type.LogLevel;
import log.server.storm.bolt.base.ExportBolt;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-22.
 */
public class MongoDBExportBolt extends ExportBolt {
    private static final long serialVersionUID = 1L;

    public MongoDBExportBolt() {
        super(LogLevel.ALL);
    }

    public MongoDBExportBolt(int level) {
        super(level);
    }

    @Override
    public void export(JSONObject jsonLog) {
        System.out.println("Export to MongoDB Here!");
    }
}
