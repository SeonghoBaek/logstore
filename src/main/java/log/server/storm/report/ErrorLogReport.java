package log.server.storm.report;

import log.agent.type.LogLevel;
import log.server.storm.types.LogList;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-04-27.
 */
public class ErrorLogReport implements IReport {
    private long C = 0;
    private long D = 0;
    private long E = 0;
    private long interval = 0;

    LogList Summary = null;

    public ErrorLogReport() {
        Summary = new LogList();
    }

    public void setC(long c) {
        this.C = c;
    }

    public void setD(long d) {
        this.D = d;
    }

    public void setE(long e) {
        this.E = e;
    }

    public void setInterval(long i) {
        this.interval = i;
    }

    public long getC() {return this.C;}
    public long getD() {return this.D;}
    public long getE() {return this.E;}
    public long getInterval() {return this.interval;}

    public void addSummary(JSONObject obj) {
        this.Summary.add(obj);
    }

    public LogList getSummary() {
        return this.Summary;
    }

    public String getJSONString() {
        long c = this.C;
        long d = this.D;
        long e = this.E;

        String jsonReport = String.format(ReportSchema.EWMALogReportPrefix, LogLevel.ERROR, this.interval, c, d, e);

        boolean bFirst = true;

        for (JSONObject obj : this.Summary) {
            if (bFirst == true) bFirst = false;
            else jsonReport += ",";
            jsonReport += obj.toJSONString();

        }

        jsonReport += ReportSchema.EWMALogReportPostfix;

        return jsonReport;
    }
}
