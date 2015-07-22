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
import log.server.storm.spout.Beacon;
import log.server.storm.types.LogList;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by major.baek on 2015-04-22.
 */
public abstract class ProcessBolt extends BaseRichBolt {
    private OutputCollector collector;
    private int logLevel;
    private static final long serialVersionUID = 1L;

    public ProcessBolt(int level) {
        super();
        this.logLevel = level;
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple input) {
        if (input.contains(Beacon.FIELD_NAME) == true) {
            String beacon = (String)input.getValueByField(Beacon.FIELD_NAME);
            this.collector.ack(input);
            JSONObject resultObj = process(beacon);

            if (resultObj != null) {
                LogList output = new LogList();
                output.add(resultObj);
                this.collector.emit(new Values(output));
            }

        } else {
            LogList values = (LogList)input.getValueByField(LogList.TUPLE_NAME);
            LogList output = new LogList();

            if (values == null) return;

            for (JSONObject value : values) {
                //System.out.println(value.toJSONString());

                JSONObject log = (JSONObject)value.get(LogSchema.LOG);

                if (log == null) continue;

                JSONObject body = (JSONObject)log.get(LogSchema.BODY);

                if (body == null) continue;

                long level = (Long)body.get(LogSchema.LEVEL);

                if (LogLevel.isSet(this.logLevel, (int)level) == true) {

                    JSONObject resultObj = this.process(value);

                    if (resultObj != null) {
                        output.add(resultObj);
                    }
                }
            }

            this.collector.ack(input);

            if (output.size() > 0) {
                this.collector.emit(new Values(output));
            }
        }
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(LogList.TUPLE_NAME));
    }

    public abstract JSONObject process(JSONObject jsonLog);
    public abstract JSONObject process(String beacon);
}
