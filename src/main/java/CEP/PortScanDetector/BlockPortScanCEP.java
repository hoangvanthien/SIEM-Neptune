package CEP.PortScanDetector;

import Utilities.*;
import com.espertech.esper.compiler.client.*;
import com.espertech.esper.runtime.client.*;
import org.pcap4j.packet.namednumber.*;

import java.io.*;
import java.net.*;

public class BlockPortScanCEP {
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
