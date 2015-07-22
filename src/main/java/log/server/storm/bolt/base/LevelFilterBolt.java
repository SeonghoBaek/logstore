package log.server.storm.bolt.base;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import log.agent.type.LogLevel;
import log.agent.type.LogSchema;
import log.server.storm.types.LogList;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by major.baek on 2015-04-22.
 */
public class LevelFilterBolt extends BaseRichBolt {
    private int logLevel;
    private OutputCollector collector;
    private static final long serialVersionUID = 1L;

    public LevelFilterBolt(int level) {
        super();
        this.logLevel = level;
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple input) {
        LogList values = (LogList)input.getValueByField(LogList.TUPLE_NAME);
        LogList output = new LogList();

        if (values == null) return;

        for (JSONObject value : values) {
            JSONObject log = (JSONObject)value.get(LogSchema.LOG);

            if (log == null) continue;

            JSONObject body = (JSONObject)log.get(LogSchema.BODY);

            if (body == null) continue;

            long level = (Long)body.get(LogSchema.LEVEL);

            if (LogLevel.isSet(this.logLevel, (int)level) == true) {
                output.add(value);
            }
        }

        if (output.size() > 0) {
            this.collector.emit(new Values(output));
        }

        this.collector.ack(input);
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(LogList.TUPLE_NAME));
    }
}
