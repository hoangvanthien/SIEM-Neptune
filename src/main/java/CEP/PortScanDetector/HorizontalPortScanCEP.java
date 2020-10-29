package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;

public class HorizontalPortScanCEP {
    private static int period = 10;
    private static int threshold = 3;

    public static void setup() throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {
        EPAdapter.quickExecute("@public create window SinglePortScan_SYN_Latest_H.win:time("+period+") as SinglePortScan_SYN_Event",
                "insert into SinglePortScan_SYN_Latest_H select * from SinglePortScan_SYN_Event",

                "@public insert into HorizontalPortScan_Event " +
                        "select current_timestamp() as timestamp, targetAddress from SinglePortScan_SYN_Latest_H " +
                        "group by targetPort having count(distinct targetAddress) >= " + threshold,

                "on HorizontalPortScan_Event as A delete from SinglePortScan_SYN_Latest_H as B where B.targetAddress=A.targetAddress");


        new EPAdapter().execute("select * from HorizontalPortScan_Event").addListener((data, __, ___, ____) -> {
            DashboardAdapter.alertHigh(data[0].get("targetAddress") + " is under a port scan attack!");
//            System.out.println("["+Misc.formatTime((long)data[0].get("timestamp"))+"] ALERT: "+data[0].get("targetAddress") + " is under a port scan attack!");
        });
    }
}
