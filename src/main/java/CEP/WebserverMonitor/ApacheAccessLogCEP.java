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
 */


public class ApacheAccessLogCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {3, 5};
    /**
     * add listener and set priority from low to high in range
     *@throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     *@throws EPDeployException Indicate that a precondition is not satisfied
     */
    public static void setup() throws EPCompileException, EPDeployException {
        setup("LowPriority", period[0], threshold[0]);
        setup("HighPriority", period[1], threshold[1]);
        new EPAdapter().execute("select * from AAL_Event").
                addListener( (newData, __, ___, ____) -> {
                    DashboardAdapter.writeToTable(newData[0], 1);
                });

        new EPAdapter().execute("select * from AAL_Alert_FileMissing_LowPriority").
            addListener((newData, __, ___, ____) -> {
                DashboardAdapter.alertLow("Too many requests to non-existent file " + newData[0].get("url"));
        });

        new EPAdapter().execute("select * from AAL_Alert_FileMissing_HighPriority").
                addListener((newData, __, ___, ____) -> {
                    DashboardAdapter.alertHigh("Likely missing file: " + newData[0].get("url"));
                });
    }
    /**
     * setup EPL statement for executing
     *@throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     *@throws EPDeployException Indicate that a precondition is not satisfied
     */
    private static void setup(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String latest = "AAL_Latest_FileMissing_"+id;
        String alert = "AAL_Alert_FileMissing_"+id;
        EPAdapter.quickExecute(
                "@public create window "+latest+".win:time(" + period + ") as AAL_Event",
                "insert into "+latest+" select * from AAL_Event",
                "@public insert into "+alert+
                        " select url from "+latest+"(httpStatusCode='404')" +
                        " group by url having count(*) >= " + threshold,
                "on "+alert+" as a delete from "+latest+" as b where a.url=b.url"
        );
    }
    /**
     * return time interval for a scan to take place
     * @return the value of time interval
     */
    public static int[] getPeriod() {
        return period;
    }
    /**
     * set the time interval for a scan to take place
     * @param period initialized interval interval for a scan to take place
     */
    public static void setPeriod(int[] period) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.period = period;
    }
    /**
     * return the threshold of consecutive failed access log times
     * @return the value of consecutive failure access log count
     */
    public static int[] getThreshold() {
        return threshold;
    }
    /**
     * setup the maximum number of times for consecutive failed access log
     * @param threshold initialized threshold of failed access log
     */
    public static void setThreshold(int[] threshold) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.threshold = threshold;
    }
}
