package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;

public class HorizontalPortScan {
    public HorizontalPortScan(int alertPeriod, int consecutiveFailed) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        new EPAdapter().execute("get-horizontal-port-scan", "insert into HorizontalPortScanAlert\n" +
                "select tcpHeader.srcPort\n" +
                "from TCPPacket#time_batch(" + alertPeriod + " seconds)\n" +
                "group by tcpHeader.srcPort\n" +
                "having count(distinct ipHeader.dstAddr) >= " + consecutiveFailed + "");

        new EPAdapter().execute("alert-horizontal-port-scan", "select * from HorizontalPortScanAlert")
                .addListener((newData, __, ___, ____) -> {
                    Port hostAddr = (Port) newData[0].get("hostPort");
                    System.out.println("Alert: Port " + hostAddr.valueAsInt()
                            + " is under attack!");
                });
    }
}
