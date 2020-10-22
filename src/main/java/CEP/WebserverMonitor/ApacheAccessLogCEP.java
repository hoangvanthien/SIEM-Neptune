package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import Utilities.Misc;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;


public class ApacheAccessLogCEP {
    private static int period = 10;
    private static int threshold = 3;
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

    public static int getPeriod() {
        return period;
    }

    public static void setPeriod(int period) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.period = period;
    }

    public static int getThreshold() {
        return threshold;
    }

    public static void setThreshold(int threshold) {
        EPAdapter.destroy();
        ApacheAccessLogCEP.threshold = threshold;
    }
}
