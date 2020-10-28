package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

/**
 * Compile EPL statement to raise alert for Apache access log file bad request
 * An event listener is added to log the alert message
 * @author Hoang Van Thien
 * @author Lu Minh Khuong
 * @author Nguyen Hoang Quan
 */
public class ApacheAccessLogCEP {
    /**
     * @param period setup time interval
     * @param threshold the maximum times of Failure AAL
     */
    private static int period = 10;
    private static int threshold = 3;

    /**
     * setup EPL statement and execute
     *@throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     *@throws EPDeployException Indicate that a precondition is not satisfied
     */
    public static void setup() throws EPCompileException, EPDeployException {

        EPAdapter.quickExecute(
                "@public create window AAL_Latest.win:time(" + period + ") as AAL_Event",
                "insert into AAL_Latest select * from AAL_Event",
                "@public insert into AAL_FailureCount select current_timestamp() as timestamp, url, count(*) as counter from AAL_Latest(httpStatusCode like '4%') group by url",
                "@public insert into AAL_Alert select * from AAL_FailureCount(counter >= " + threshold + ")"
        );


        new EPAdapter().execute("select * from AAL_Event").
                addListener( (newData, __, ___, ____) -> {
//                    System.out.println("[" + newData[0].get("timeFormatted") + "] " +
//                            newData[0].get("clientAddress") + " sent a " + newData[0].get("requestMethod") +
//                            " to " + newData[0].get("url") + " and got status code " + newData[0].get("httpStatusCode"));
                    DashboardAdapter.writeToTable(newData[0], 1);
                });

        new EPAdapter().execute("select * from AAL_Alert").
            addListener((newData, __, ___, ____) -> {
                System.out.println("[" + Misc.formatTime((long)newData[0].get("timestamp")) + "] ALERT: There have been too many " +
                        "bad requests to " + newData[0].get("url"));
        });
    }

    /**
     * return time interval condition of failed access log alert
     * @return the value of time interval
     */
    public static int getPeriod() {
        return period;
    }

    /**
     * set the interval condition for failed access log alert
     * @param period initialized interval condition of alert
     */
    public static void setPeriod(int period) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.period = period;
    }

    /**
     * return the threshold of failed access log times
     * @return the value of failure access log count
     */
    public static int getThreshold() {
        return threshold;
    }

    /**
     * setup the maximum number of times for failed access log
     * @param threshold initialized threshold of failed access log
     */
    public static void setThreshold(int threshold) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.threshold = threshold;
    }
}
