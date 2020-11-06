package CEP.PortScanDetector;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.io.File;
import java.io.IOException;

/**
 * Facade class to set up the CEP Engine to analyze and classify the incoming TCP Packet
 */
public class SinglePortScanCEP {
    /**
     * Set up the event streams in the CEP Engine with EPL Statement and some listeners
     * @throws EPCompileException
     * @throws EPDeployException
     * @throws IOException
     * @throws ParseException
     */
    public static void setup() throws EPCompileException, EPDeployException, IOException, ParseException {
        EPAdapter.executeFile("SinglePortScan.epl");
        new EPAdapter().execute("select * from SinglePortScan_SYN_Event").addListener((data, __, ___, ____) -> {
            DashboardAdapter.writeToTable(data[0], DashboardAdapter.PORT_SCAN_TABLE);
        });
    }
}
