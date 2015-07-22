package log.server.storm.analysis;

import org.json.simple.JSONObject;

import java.io.Serializable;

/**
 * Created by major.baek on 2015-04-23.
 */
public interface IAnalyse extends Serializable {
    public JSONObject analyse(JSONObject jsonLog);
}
