package CEP.PortScanDetector;


import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;

import java.io.*;
import java.net.*;

public class VerticalPortScanCEP {
    public VerticalPortScanCEP(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        new EPAdapter().execute("get-vertical-port-scan", "insert into VerticalPortScanAlert\n" +
                "select ipHeader.dstAddr\n" +
                "from TCPPacket#time_batch(" + alertPeriod + " seconds)#expr(oldest_timestamp > newest_timestamp - 10000)\n" +
                "group by ipHeader.dstAddr\n" +
                "having count(distinct tcpHeader.dstPort) > " + consecutiveFailed + "");

        new EPAdapter().execute("alert-vertical-port-scan", "select * from VerticalPortScanAlert")
                .addListener((newData, __, ___, ____) -> {
                    InetAddress hostAddr = (InetAddress) newData[0].get("hostAddr");
                    System.out.println("Alert a vertical scan: IP " + hostAddr.getHostAddress()
                        + " is under attack!");
        });
    }
}
