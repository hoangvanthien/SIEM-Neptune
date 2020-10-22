package CEP.PortScanDetector;


import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;

import java.io.*;
import java.net.*;

public class VerticalPortScan {
    public VerticalPortScan(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        new EPAdapter().execute("get-vertical-port-scan", "insert into VerticalPortScanAlert\n" +
                "select ipHeader.dstAddr\n" +
                "from TCPPacket#time_batch(" + alertPeriod + " seconds)\n" +
                "group by ipHeader.dstAddr\n" +
                "having count(distinct tcpHeader.dstPort) > " + consecutiveFailed + "");

        new EPAdapter().execute("alert-vertical-port-scan", "select * from VerticalPortScanAlert")
                .addListener((newData, __, ___, ____) -> {
                    InetAddress hostAddr = (InetAddress) newData[0].get("hostAddr");
                    System.out.println("Alert: IP " + hostAddr.getHostAddress()
                        + " is under attack!");
        });
    }
}
