package CEP.PortScanDetector;


import Dashboard.Dashboard;
import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.TcpPacket;

import java.io.*;
import java.net.*;
/**
 * Compile the EPL statement for raising alerts for vertical port scan events that might be happen
 * An event listener is also attached to log the alert messages to the user.
 * @author Lu Minh Khuong
 */
public class VerticalPortScanCEP {
    private static int[] period = {10, 10};
    private static int[] threshold = {200, 500};
    /**
     * add listener and setup priority from low to high
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
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
    /**
     * setup EPL statement
     * @param id port number of detected port
     * @param period time interval for port scanning
     * @param threshold the maximum of consecutive port scan detection
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     */
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
     * return the time interval of port scan
     * @return the value of time interval port scan
     */
    public static int[] getPeriod() {
        return period;
    }
    /**
     * set the time interval of port scan
     * @param period instance for time interval
     */
    public static void setPeriod(int[] period) {
        VerticalPortScanCEP.period = period;
    }
    /**
     * return the threshold port scan detection
     * @return the value of threshold
     */
    public static int[] getThreshold() {
        return threshold;
    }
    /**
     * set the threshold of port scan
     * @param threshold instance of threshold for port scan
     */
    public static void setThreshold(int[] threshold) {
        VerticalPortScanCEP.threshold = threshold;
    }
}
