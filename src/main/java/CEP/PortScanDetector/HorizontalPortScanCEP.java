package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;

public class HorizontalPortScanCEP {
    public HorizontalPortScanCEP(int alertPeriod, int consecutiveFailed, int interval) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        EPAdapter.quickExecute("@public insert into HorizontalPortScanAlert\n" +
                "select tcpHeader.srcPort\n" +
                "from TCPPacket#time(" + alertPeriod + " seconds)#expr(oldest_timestamp > newest_timestamp - 10000)\n" +
                "group by tcpHeader.srcPort\n" +
                "having count(distinct ipHeader.dstAddr) >= " + consecutiveFailed +
                "output first every " + interval + " seconds" );

        new EPAdapter().execute("alert-horizontal-port-scan", "select * from HorizontalPortScanAlert")
                .addListener((newData, __, ___, ____) -> {
                    Port hostAddr = (Port) newData[0].get("hostPort");
                    System.out.println("Alert a horizontal scan: Port " + hostAddr.valueAsInt()
                            + " is under attack!");
                });
    }
}
