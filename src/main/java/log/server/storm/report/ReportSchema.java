package log.server.storm.report;

import log.agent.type.LogSchema;

/**
 * Created by major.baek on 2015-04-23.
 */
public class ReportSchema {
    public static final String LEVEL = LogSchema.LEVEL;
    public static final String INTERVAL = "interval";
    public static final String COUNT = "count";
    public static final String DIFF = "diff";
    public static final String EXPECT = "expect";
    public static final String SUMMARY = "summary";
    public static final String URI = LogSchema.URI;

    public static final String EWMALogReportPrefix = "{\"" + LogSchema.BODY + "\":{\"" + LEVEL + "\":\"%s\",\"" + INTERVAL + "\":%s,\"" + COUNT +
                                                        "\":%s,\"" + DIFF + "\":%s,\"" + EXPECT + "\":%s,\"" + SUMMARY + "\":[";
    public static final String EWMALogReportPostfix = "]}}";
    public static final String EWMALogReportSummary = "{\"" + URI + "\":\"%s\",\"" + COUNT + "\":%s,\"" + DIFF + "\":%s}";
}