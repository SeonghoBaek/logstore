package log.agent.interceptor;

import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-05-12.
 */
public interface IInterceptor {
    public void initialize();
    public JSONObject intercept(JSONObject logObject);
}
