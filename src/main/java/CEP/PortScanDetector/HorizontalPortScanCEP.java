package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;

/**
 * Facade class to set up the CEP Engine to catch Horizontal Port Scan
 * The setup() function must be called after SinglePortScanCEP.setup()
 * @author Khuong Lu, Thien Hoang
 */
public class HorizontalPortScanCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {2, 5};

    /**
     * Set up the event streams in the CEP Engine with EPL Statement and some listeners
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static void setup() throws EPCompileException, EPDeployException {
        setup("LowPriority", period[0], threshold[0]);
        setup("HighPriority", period[1], threshold[1]);

        new EPAdapter().execute("select * from HorizontalPortScan_Alert_LowPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertLow("Failed to establish TCP connection with port "+data[0].get("targetPort")+" too many times");
        });

        new EPAdapter().execute("select * from HorizontalPortScan_Alert_HighPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertHigh("Port " + data[0].get("targetPort") + " is under a horizontal port scan.");
        });
    }
    private static void setup(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String latest = "SinglePortScan_SYN_Latest_H_" + id;
        String alert = "HorizontalPortScan_Alert_" + id;
        EPAdapter.quickExecute("@public create window "+latest+".win:time("+period+") as SinglePortScan_SYN_Event",
                "insert into "+latest+" select * from SinglePortScan_SYN_Event",

                "@public insert into "+alert+
                        " select targetPort from "+latest+
                        " group by targetPort having count(distinct targetAddress) >= " + threshold,

                "on "+alert+" as A delete from "+latest+" as B where B.targetPort=A.targetPort");
    }

    /**
     * Get the current periods after which old packets (used to detect Horizontal Port Scan) will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getPeriod() {
        return period;
    }

    /**
     * Set the new periods after which old packets (used to detect Horizontal Port Scan) will expire
     * @param period [period_lowPriority, period_highPriority]
     */
    public static void setPeriod(int[] period) {
        EPAdapter.destroy();
        HorizontalPortScanCEP.period = period;
    }

    /**
     * Get the current thresholds (number of different machines that got scanned) over which a horizontal port scan alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getThreshold() {
        return threshold;
    }

    /**
     * Set the new thresholds (number of different machines that got scanned) over which a horizontal port scan alert will be raised
     * @param threshold [threshold_lowPriority, threshold_highPriority]
     */
    public static void setThreshold(int[] threshold) {
        EPAdapter.destroy();
        HorizontalPortScanCEP.threshold = threshold;
    }
}
