package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;


public class ApacheAccessLogCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {3, 5};
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

        new EPAdapter().execute("select * from AAL_Alert_FileMissing_LowPriority").
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
                "on "+alert+" delete from "+latest+""
        );
    }

    public static int[] getPeriod() {
        return period;
    }

    public static void setPeriod(int[] period) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.period = period;
    }

    public static int[] getThreshold() {
        return threshold;
    }

    public static void setThreshold(int[] threshold) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.threshold = threshold;
    }
}
