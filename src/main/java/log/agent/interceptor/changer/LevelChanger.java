package log.agent.interceptor.changer;

import log.agent.interceptor.IInterceptor;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-05-12.
 */
public class LevelChanger implements IInterceptor {
    public void initialize() {
        System.out.println("LevelChanger initialized");
    }

    public JSONObject intercept(JSONObject logObject) {
        return null;
    }
}
