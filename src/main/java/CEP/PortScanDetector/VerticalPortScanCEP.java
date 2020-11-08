package CEP.PortScanDetector;


import Dashboard.Dashboard;
import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.TcpPacket;

import java.io.*;
import java.net.*;

/**
 * Facade class to set up the CEP Engine to detect Vertical Port Scan
 */
public class VerticalPortScanCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {200, 500};

    /**
     * Set up the event streams in the CEP Engine with EPL Statement and some listeners
     * @throws EPCompileException
     * @throws EPDeployException
     */
    public static void setup() throws EPCompileException, EPDeployException {
        setup("LowPriority", period[0], threshold[0]);
        setup("HighPriority", period[1], threshold[1]);

        new EPAdapter().execute("select * from VerticalPortScan_Alert_LowPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertLow("Failed to establish TCP connection with "+data[0].get("targetAddress")+" too many times");
        });

        new EPAdapter().execute("select * from VerticalPortScan_Alert_HighPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertHigh(data[0].get("targetAddress") + " is under a vertical port scan.");
        });
    }
    private static void setup(String id, int period, int threshold) throws EPCompileException, EPDeployException {
        String latest = "SinglePortScan_SYN_Latest_V_" + id;
        String alert = "VerticalPortScan_Alert_" + id;
        EPAdapter.quickExecute("@public create window "+latest+".win:time("+period+") as SinglePortScan_SYN_Event",
                "insert into "+latest+" select * from SinglePortScan_SYN_Event",

                "@public insert into "+alert+
                        " select targetAddress from "+latest+
                        " group by targetAddress having count(distinct targetPort) >= " + threshold,

                "on "+alert+" as A delete from "+latest+" as B where B.targetAddress=A.targetAddress");
    }

    /**
     * Get the current periods after which old packets (used to detect Vertical Port Scan) will expire
     * @return [period_lowPriority, period_highPriority]
     */
    public static int[] getPeriod() {
        return period;
    }

    /**
     * Set the new periods after which old packets (used to detect Vertical Port Scan) will expire
     * @param period [period_lowPriority, period_highPriority]
     */
    public static void setPeriod(int[] period) {
        EPAdapter.destroy();
        VerticalPortScanCEP.period = period;
    }

    /**
     * Get the current thresholds (number of different ports that got scanned) over which a horizontal port scan alert will be raised
     * @return [threshold_lowPriority, threshold_highPriority]
     */
    public static int[] getThreshold() {
        return threshold;
    }

    /**
     * Set the new thresholds (number of different ports that got scanned) over which a horizontal port scan alert will be raised
     * @param threshold
     */
    public static void setThreshold(int[] threshold) {
        EPAdapter.destroy();
        VerticalPortScanCEP.threshold = threshold;
    }
}
