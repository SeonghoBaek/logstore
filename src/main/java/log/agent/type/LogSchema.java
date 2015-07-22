package log.agent.type;

/**
 * Created by major.baek on 2015-04-27.
 */
public class LogSchema {
    public static final String TYPE = "type"; // log type. line log or transaction log
    public static final String START = "start"; // transaction log start time
    public static final String END = "end"; // transaction log end time
    public static final String LOG = "log"; // log array
    public static final String BODY = "body";
    public static final String HEADER = "header";
    public static final String TIME = "time";
    public static final String HOST = "host";
    public static final String URI = "uri";
    public static final String LEVEL = "level";
    public static final String TEXT = "text";
    public static final int TRANSACTION_TYPE = 1;
    public static final int LINE_TYPE = 0;

    public static final String DEVICE_ID = "devid";
    public static final String MAC_ADDR = "macaddr";
    public static final String USER_ID = "userid";
}