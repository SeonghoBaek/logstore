package log.agent.interceptor.alerter;

import log.agent.interceptor.IInterceptor;
import log.agent.type.LogSchema;
import org.json.simple.JSONObject;

/**
 * Created by major.baek on 2015-05-12.
 */
public class TransactionTimeChecker implements IInterceptor {

    public void initialize() {
        System.out.println("TransactionTimeChecker initialized");
    }

    public JSONObject intercept(JSONObject logObject) {
        try {
            int logType = (Integer)logObject.get(LogSchema.TYPE);

            if (logType == LogSchema.TRANSACTION_TYPE) {
                long startTime = (Long) logObject.get(LogSchema.START);
                long endTime = (Long) logObject.get(LogSchema.END);

                if ((endTime - startTime) > 100) {
                    System.out.println(Thread.currentThread().getId() + " : TOO much time..Over 100 ms");
                }
            }
        } catch (Exception e) {
            e.toString();
        }

        return null;
    }
}
