package log.server.storm.bolt;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import log.agent.type.LogSchema;
import log.server.storm.types.LogList;
import org.json.simple.JSONObject;

import java.util.Map;

/**
 * Created by major.baek on 2015-05-06.
 */
public class TransactionTimeAlerter extends BaseRichBolt {
    private long threashold;
    private OutputCollector collector;
    private static final long serialVersionUID = 1L;

    public TransactionTimeAlerter(long threashold) {
        super();
        this.threashold = threashold;
    }

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
    }

    public void execute(Tuple input) {
        LogList values = (LogList)input.getValueByField(LogList.TUPLE_NAME);
        LogList output = new LogList();

        if (values == null) return;

        for (JSONObject value : values) {
            long type = (Long)value.get(LogSchema.TYPE);

            if ((int)type == LogSchema.LINE_TYPE) continue;

            long start = (Long)value.get(LogSchema.START);
            long end = (Long)value.get(LogSchema.END);
            long duration = end - start;

            if (duration > this.threashold) {
                System.out.println("Alert! Transaction Processing Time: " + duration);
                System.out.println(value.toJSONString());

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
