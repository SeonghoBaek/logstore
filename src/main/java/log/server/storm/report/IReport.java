package log.server.storm.report;

import log.server.storm.types.LogList;
import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * Created by major.baek on 2015-04-23.
 */
public interface IReport extends Serializable {
    public String   getJSONString();
    public void     addSummary(JSONObject jsonObject);
    public LogList  getSummary();
}
