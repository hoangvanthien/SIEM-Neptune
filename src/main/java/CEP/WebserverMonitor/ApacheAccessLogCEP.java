package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

/**
 * Facade class to set up the CEP Engine to analyze the requests made to the Webserver
 */
public class ApacheAccessLogCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {3, 5};

    /**
     * Set up the event streams in the CEP Engine with EPL Statement and some listeners
     * @throws EPCompileException
     * @throws EPDeployException
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
     * Get the current periods after which old events will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getPeriod() {
        return period;
    }

    /**
     * Set the new periods after which old events will expire
     * @param period [period_lowPriority, period_highPriority]
     */
    public static void setPeriod(int[] period) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.period = period;
    }

    /**
     * Get the current thresholds (number of 404 responses) over which a resource missing alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getThreshold() {
        return threshold;
    }

    /**
     * Set the new thresholds (number of 404 responses) over which a resource missing alert will be raised
     * @param threshold [threshold_lowPriority, threshold_highPriority]
     */
    public static void setThreshold(int[] threshold) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.threshold = threshold;
    }
}
