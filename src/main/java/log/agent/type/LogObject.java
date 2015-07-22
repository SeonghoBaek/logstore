package log.agent.type;

import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-05-07.
 */
public class LogObject extends JSONObject {
    private static final String UNDEFINED = "NA";

    public void setURI(String uri) {
        this.put(LogSchema.URI, uri);
    }

    public String getURI() {
        String uri = (String)this.get(LogSchema.URI);

        if (uri == null) uri = this.UNDEFINED;

        return uri;
    }

    public void setUserID(String userId) {
        this.put(LogSchema.USER_ID, userId);
    }

    public String getUserId() {
        String userId = (String)this.get(LogSchema.USER_ID);

        if (userId == null) userId = this.UNDEFINED;

        return userId;
    }

    public void setDeviceID(String deviceId) {
        this.put(LogSchema.DEVICE_ID, deviceId);
    }

    public String getDeviceID() {
        String devId = (String)this.get(LogSchema.DEVICE_ID);

        if (devId == null) devId = this.UNDEFINED;

        return devId;
    }

    public void setMacAddr(String macAddr) {
        this.put(LogSchema.MAC_ADDR, macAddr);
    }

    public String getMacAddr() {
        String macAddr = (String)this.get(LogSchema.MAC_ADDR);

        if (macAddr == null) macAddr = this.UNDEFINED;

        return macAddr;
    }

    public void setEvent(Object obj) {
        this.put(LogSchema.TEXT, obj);
    }

    public Object getEvent() {
        Object event = this.get(LogSchema.TEXT);

        if (event == null) event = this.UNDEFINED;

        return event;
    }
}
