package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;

/**
 * Compile EPL statements for the HorizontalPortScanAlert event
 * A listener is attached to log message
 * @author Lu Minh Khuong
 */
public class HorizontalPortScanCEP {
    /**
     *
     * @param alertPeriod time for a port scan to take place
     * @param consecutiveFailed number of consecutive failed
     * @param interval the time interval to print the result
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     * @throws IOException Indicate that failed or interrupted I/O operations
     */
    public HorizontalPortScanCEP(int alertPeriod, int consecutiveFailed, int interval) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        new EPAdapter().execute("get-horizontal-port-scan", "insert into HorizontalPortScanAlert\n" +
                "select tcpHeader.dstPort\n" +
                "from TCPPacket#time(" + alertPeriod + " seconds)#expr(oldest_timestamp > newest_timestamp - 10000)\n" +
                "group by tcpHeader.dstPort\n" +
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
