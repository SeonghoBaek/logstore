package log.server.storm.bolt.base;

import backtype.storm.task.OutputCollector;
import backtype.storm.task.TopologyContext;
import backtype.storm.topology.OutputFieldsDeclarer;
import backtype.storm.topology.base.BaseRichBolt;
import backtype.storm.tuple.Fields;
import backtype.storm.tuple.Tuple;
import backtype.storm.tuple.Values;
import log.server.storm.types.LogList;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.util.Map;

/**
 * Created by major.baek on 2015-04-27.
 */
public class JSONConverterBolt extends BaseRichBolt {
    private OutputCollector     collector;
    private JSONParser          jsonParser;
    private static final long serialVersionUID = 1L;

    public void prepare(Map stormConf, TopologyContext context, OutputCollector collector) {
        this.collector = collector;
        this.jsonParser = new JSONParser();
    }

    public void execute(Tuple input) {
        Object logObj = input.getValue(0);
        String logString;

        if (logObj instanceof String) {
            logString = (String) logObj;
        } else {
            byte[] bytes = (byte[]) logObj;

            try {
                logString = new String(bytes, "UTF-8");
            } catch (UnsupportedEncodingException e) {
                throw new RuntimeException(e);
            }
        }

        int s = -1;
        int numS = 0;

        LogList logList = new LogList();

        for (int i = 0; i < logString.length(); i++) {
            if (logString.charAt(i) == '{') {
                if (s == -1) s = i;
                numS++;
            } else if (logString.charAt(i) == '}') {
                numS--;

                if (numS == 0) {
                    String jsonLog = logString.substring(s, i + 1);
                    s = -1;

                    try {
                        JSONObject log = (JSONObject) this.jsonParser.parse(jsonLog);

                        logList.add(log);
                    } catch (ParseException pe) {
                        pe.printStackTrace();
                    }
                }
            }
        }

        this.collector.ack(input);
        this.collector.emit(new Values(logList));
    }

    public void declareOutputFields(OutputFieldsDeclarer declarer) {
        declarer.declare(new Fields(LogList.TUPLE_NAME));
    }
}
