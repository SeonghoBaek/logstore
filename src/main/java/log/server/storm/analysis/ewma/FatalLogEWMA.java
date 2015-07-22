package log.server.storm.analysis.ewma;

import log.agent.type.LogSchema;
import log.server.storm.analysis.IAnalyse;
import log.server.storm.report.FatalLogReport;
import log.server.storm.report.ReportSchema;
import log.util.Time;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import redis.clients.jedis.JedisPool;

import java.util.HashMap;
import java.util.Set;

/**
 * Created by major.baek on 2015-04-22.
 */
public class FatalLogEWMA implements IAnalyse {
    private static final long serialVersionUID = 1L;

    private EWMA ewma;
    private HashMap<String, URILog> uriMap = null;

    private long    C = 0;
    private long    D = 0;
    private long    E = 0;
    private long    interval = 5 * Time.SECOND.getTime();
    private long    elapsed = 0;
    private long    logStartTime = 0;
    private long    logEndTime = 0;

    private static JedisPool pool = null;
/*
    static {
        pool = new JedisPool(new JedisPoolConfig(), "wilson");
    }
*/

    public FatalLogEWMA(long interval) {
        this.interval = interval;
        //this.ewma = new EWMA().sliding(1.0d, Time.MINUTE).withAlphaWindow(1.0d, Time.MINUTE);
        this.ewma = new EWMA().sliding(1.0d, Time.MINUTE).withAlpha(EWMA.ONE_MINUTE_ALPHA);
        this.uriMap = new HashMap<String, URILog>();
    }

    public FatalLogEWMA() {
        //this.ewma = new EWMA().sliding(1.0d, Time.MINUTE).withAlphaWindow(1.0d, Time.MINUTE);
        this.ewma = new EWMA().sliding(1.0d, Time.MINUTE).withAlpha(EWMA.ONE_MINUTE_ALPHA);
        this.uriMap = new HashMap<String, URILog>();
    }

    public JSONObject createReport() {
        if (this.logStartTime == 0) {
            this.logStartTime = this.logEndTime = System.currentTimeMillis();

            return null;
        } else {
            this.logEndTime = System.currentTimeMillis();
        }

        if (this.logEndTime - this.logStartTime == 0) return null;

        //System.out.println("REPORT DAEMON");
        FatalLogReport report = new FatalLogReport();
        //Jedis jedis = pool.getResource();
        JSONParser  jsonParser = new JSONParser();

        this.ewma.markCountsInTime(this.C, this.logEndTime - this.logStartTime);
        double rate = this.ewma.getAverageRatePer(Time.SECOND);
        this.E = (long)rate;

        this.D = this.C - this.D;

        report.setInterval(this.logEndTime - this.logStartTime);
        this.logStartTime = this.logEndTime;
        report.setC(this.C);
        report.setD(this.D);
        report.setE(this.E);

        this.D = this.C; // Back up.
        this.C = 0;

        Set<String> keySet =  this.uriMap.keySet();

        if (keySet.size() == 0) return null;

        for (String key : keySet) {
            URILog log = this.uriMap.get(key);

            long diff = log.C - log.D;

            String jsonString = String.format(ReportSchema.EWMALogReportSummary, key, log.C, diff);

            log.D = log.C;
            log.C = 0;

            this.uriMap.put(key, log);

            try {
                JSONObject jsonObj = (JSONObject)jsonParser.parse(jsonString);
                report.addSummary(jsonObj);
            } catch (ParseException pe) {
                pe.printStackTrace();
            }
        }

        System.out.println(report.getJSONString());

        try {
            JSONObject jsonObj = (JSONObject)jsonParser.parse(report.getJSONString());

            return jsonObj;
        } catch (ParseException pe) {
            pe.printStackTrace();
        }

        return null;
        //jedis.set("criticallogreport", this.report.getJSONString());

        //pool.returnResource(jedis);
    }

    class URILog {
        public long C = 0;
        public long D = 0;
    }

    public JSONObject analyse(JSONObject jsonLog) {
        //System.out.println(jsonLog.toJSONString());
        long logType = (Long)jsonLog.get(LogSchema.TYPE);

        if ((int)logType == LogSchema.TRANSACTION_TYPE) {
            return null;
        }

        JSONObject log = (JSONObject)jsonLog.get(LogSchema.LOG);

        JSONObject logHeader = (JSONObject)log.get(LogSchema.HEADER);
        JSONObject report = null;

        if (logHeader == null) return null;

        long time = (Long)logHeader.get(LogSchema.TIME);

        /*
        if (this.elapsed == 0) {
            this.elapsed = System.currentTimeMillis();
            this.logStartTime = time;
        } else {
            long now = System.currentTimeMillis();
            if (now - this.elapsed > this.interval) {
                this.logEndTime = time;
                report = this.createReport();
                this.logStartTime = time;
                this.elapsed = now;
            }
        }
        */

        //this.ewma.mark(time);
        //double rate = this.ewma.getAverageRatePer(Time.MINUTE);

        // Total
        this.C++;
        //this.E = (long)rate;

        // By URI
        String uri = (String)logHeader.get(LogSchema.URI);
        URILog uriLog = this.uriMap.get(uri);

        if (uriLog == null) {
            uriLog = new URILog();
            uriLog.C = 1;
            uriLog.D = 0;
        } else {
            uriLog.C++;
        }

        this.uriMap.put(uri, uriLog);

        return null;
    }
}
