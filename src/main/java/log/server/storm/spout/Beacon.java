package log.server.storm.spout;

import backtype.storm.spout.SpoutOutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichSpout;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Values;
import log.util.Util;

import java.util.Map;

/**
 * Created by major.baek on 2015-04-29.
 */
public class Beacon extends BaseRichSpout {
    private SpoutOutputCollector collector;
    public static String FIELD_NAME = "beacon";

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(FIELD_NAME));
    }

    public void open(Map conf, TopologyContext context, SpoutOutputCollector collector) {
        this.collector = collector;
    }

    // Send beacon per 1000 mili
    public void nextTuple() {
        Util.waitForMillis(1000);
        collector.emit(new Values("1000"));
    }
}
