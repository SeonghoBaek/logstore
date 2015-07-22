package log.server.storm.types;

import org.json.simple.JSONObject;

import java.util.ArrayList;

/**
 * Created by major.baek on 2015-04-27.
 */
public class LogList extends ArrayList<JSONObject> {
    public static final String TUPLE_NAME = "_LOG";
    public LogList() {
        super();
    }

    public LogList(int initCapacity) {
        super(initCapacity);
    }
}
