package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;

public class HorizontalPortScanCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {2, 5};
    public static void setup() throws EPCompileException, EPDeployException {
        setup("LowPriority", period[0], threshold[0]);
        setup("HighPriority", period[1], threshold[1]);

        new EPAdapter().execute("select * from HorizontalPortScan_Alert_LowPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertLow("Failed to establish TCP connection with port "+data[0].get("targetPort")+" too many times");
        });

        new EPAdapter().execute("select * from HorizontalPortScan_Alert_HighPriority").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertHigh("Port " + data[0].get("targetAddress") + " is under a horizontal port scan.");
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

                "on "+alert+" as A delete from "+latest+" as B where B.targetAddress=A.targetAddress");
    }

    public static int[] getPeriod() {
        return period;
    }

    public static void setPeriod(int[] period) {
        HorizontalPortScanCEP.period = period;
    }

    public static int[] getThreshold() {
        return threshold;
    }

    public static void setThreshold(int[] threshold) {
        HorizontalPortScanCEP.threshold = threshold;
    }
}
