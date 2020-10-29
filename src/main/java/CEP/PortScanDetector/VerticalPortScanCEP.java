package CEP.PortScanDetector;


import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.TcpPacket;

import java.io.*;
import java.net.*;

public class VerticalPortScanCEP {
    private static int period = 10;
    private static int threshold = 100;
    public static void setup() throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        EPAdapter.quickExecute("@public create window SinglePortScan_SYN_Latest.win:time("+period+") as SinglePortScan_SYN_Event",
                "insert into SinglePortScan_SYN_Latest select * from SinglePortScan_SYN_Event",

                "@public insert into VerticalPortScan_Event " +
                "select current_timestamp() as timestamp, targetAddress from SinglePortScan_SYN_Latest " +
                "group by targetAddress having count(distinct targetPort) >= " + threshold,

                "on VerticalPortScan_Event as A delete from SinglePortScan_SYN_Latest as B where B.targetAddress=A.targetAddress");

        new EPAdapter().execute("select * from SinglePortScan_SYN_Event").addListener((data, __, ___, ____) -> {
            DashboardAdapter.writeToTable(data[0], DashboardAdapter.PORT_SCAN_TABLE);
        });

        new EPAdapter().execute("select * from VerticalPortScan_Event").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alert(data[0].get("targetAddress") + " is under a port scan attack!");
//            System.out.println("["+Misc.formatTime((long)data[0].get("timestamp"))+"] ALERT: "+data[0].get("targetAddress") + " is under a port scan attack!");
        });
    }

    public static int getPeriod() {
        return period;
    }

    public static void setPeriod(int period) {
        VerticalPortScanCEP.period = period;
    }

    public static int getThreshold() {
        return threshold;
    }

    public static void setThreshold(int threshold) {
        VerticalPortScanCEP.threshold = threshold;
    }
}
