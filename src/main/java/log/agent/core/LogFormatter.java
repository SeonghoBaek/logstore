package log.agent.core;

import log.agent.type.LogObject;
import log.agent.type.LogSchema;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

import java.net.InetAddress;

/**
 * Created by major.baek on 2015-04-17.
 */
public class LogFormatter {
    private static String host = "";

    static {
        try {
            InetAddress haddr = InetAddress.getLocalHost();
            host = haddr.getHostAddress();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @SuppressWarnings("unchecked")
    public static JSONObject formatJSON(int level, LogObject logObject) {

        if (logObject == null) return null;

        JSONObject header = new JSONObject();

        header.put(LogSchema.URI, logObject.getURI());
        header.put(LogSchema.HOST, host);
        header.put(LogSchema.TIME, System.currentTimeMillis());

        header.put(LogSchema.DEVICE_ID, logObject.getDeviceID());
        header.put(LogSchema.MAC_ADDR, logObject.getMacAddr());
        header.put(LogSchema.USER_ID, logObject.getUserId());

        JSONObject body = new JSONObject();

        Object event = logObject.getEvent();

        if (event instanceof Throwable) {
            StackTraceElement[] steArray = ((Throwable) event).getStackTrace();
            if (steArray == null) return null;

            StringBuilder errorText = new StringBuilder();

            for (StackTraceElement std : steArray) {
                errorText.append(std.toString());
                errorText.append(", ");
            }

            errorText.append(((Throwable)event).toString());

            body.put(LogSchema.TEXT, errorText.toString());
        } else {
            body.put(LogSchema.TEXT, event);
        }

        body.put(LogSchema.LEVEL, level);

        JSONObject log = new JSONObject();

        log.put(LogSchema.BODY, body);
        log.put(LogSchema.HEADER, header);

        return log;
    }

    @SuppressWarnings("unchecked")
    public static JSONObject formatJSON(int level, String uri, Object ... events) {
        if (uri == null) return null;
        if (events == null) return null;
        if (events.length < 1) return null;

        JSONObject header = new JSONObject();

        header.put(LogSchema.URI, uri);
        header.put(LogSchema.HOST, host);
        header.put(LogSchema.TIME, System.currentTimeMillis());

        JSONObject body = new JSONObject();

        if (events[0] instanceof  Throwable) {
            StackTraceElement[] steArray = ((Throwable) events[0]).getStackTrace();
            if (steArray == null) return null;

            StringBuilder errorText = new StringBuilder();

            for (StackTraceElement std : steArray) {
                errorText.append(std.toString());
                errorText.append(", ");
            }

            errorText.append(((Throwable)events[0]).toString());

            body.put(LogSchema.TEXT, errorText.toString());
        } else {
            body.put(LogSchema.TEXT, events[0].toString());
        }

        body.put(LogSchema.LEVEL, level);

        JSONObject log = new JSONObject();

        log.put(LogSchema.BODY, body);
        log.put(LogSchema.HEADER, header);

        return log;
    }

    public static JSONObject packetizeJSON(int type, long start, long end, JSONArray log) {
        if (type == LogSchema.TRANSACTION_TYPE) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(LogSchema.LOG, log);
            jsonObject.put(LogSchema.END, end);
            jsonObject.put(LogSchema.START, start);
            jsonObject.put(LogSchema.TYPE, LogSchema.TRANSACTION_TYPE);

            return jsonObject;
        } else {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(LogSchema.TYPE, LogSchema.LINE_TYPE);
            jsonObject.put(LogSchema.LOG, log);

            return jsonObject;
        }
    }

    public static JSONObject packetizeJSON(int type, long start, long end, JSONObject log) {
        if (type == LogSchema.TRANSACTION_TYPE) {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(LogSchema.LOG, log);
            jsonObject.put(LogSchema.END, end);
            jsonObject.put(LogSchema.START, start);
            jsonObject.put(LogSchema.TYPE, LogSchema.TRANSACTION_TYPE);

            return jsonObject;
        } else {
            JSONObject jsonObject = new JSONObject();

            jsonObject.put(LogSchema.LOG, log);
            jsonObject.put(LogSchema.TYPE, LogSchema.LINE_TYPE);

            return jsonObject;
        }
    }
}
