package CEP.PortScanDetector;


import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;

import java.io.*;
import java.net.*;

/**
 * Compile the EPL statement for raising alerts for vertical port scan events that might be happen
 * An event listener is also attached to log the alert messages to the user.
 * @author Lu Minh Khuong
 */
public class VerticalPortScanCEP {
    /**
     *
     * @param alertPeriod time for a port scan to take place
     * @param consecutiveFailed number of consecutive failed of port scan
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     * @throws IOException Indicate that failed or interrupted I/O operations
     */
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
