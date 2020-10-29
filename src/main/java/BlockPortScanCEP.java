package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;
import java.net.*;
/**
 * Compile the EPL statement for raising alerts for block port scan events that might be happen
 * An event listener is also attached to log the alert messages to the user.
 * @author Lu Minh Khuong
 */
public class BlockPortScanCEP {
    /**
     * setup EPL statement
     * @param alertPeriod time for a port scan to take place
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     * @throws IOException Indicate that failed or interrupted I/O operations
     */
    public BlockPortScanCEP(int alertPeriod, int interval) throws EPCompileException, EPDeployException, IOException, EPCompileException, EPDeployException {

        new EPAdapter().execute("add-horizontal-port-scan", "insert into BlockPortScanAlert\n" +
                "select hostPort from HorizontalPortScanAlert");

        new EPAdapter().execute("add-vertical-port-scan", "insert into BlockPortScanAlert\n" +
                "select hostAddr from VerticalPortScanAlert");

        new EPAdapter().execute("alert-block-port-scan", "select * from BlockPortScanAlert#time( " + alertPeriod + " seconds)#expr(oldest_timestamp > newest_timestamp - 1000)\n" +
                "where exists(select * from HorizontalPortScanAlert)\n" +
                "and exists(select * from VerticalPortScanAlert)\n"
                ).addListener((newData, __, ___, ____) -> System.out.println("Alert a block scan:"
                        + " is happened!"));
//        new EPAdapter().execute("alert-block-port-scan", "on BlockPortScanAlert delete from BlockPortScanAlert\n");
    }
}
