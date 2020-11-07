package CEP.PortScanDetector;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.io.File;
import java.io.IOException;
/**
 * filter the open port and close port and put into port scan table
 * @author Hoang Van Thien
 */
public class SinglePortScanCEP {
    public static void setup() throws EPCompileException, EPDeployException, IOException, ParseException {
        EPAdapter.executeFile("SinglePortScan.epl");
        new EPAdapter().execute("select * from SinglePortScan_SYN_Event").addListener((data, __, ___, ____) -> {
            DashboardAdapter.writeToTable(data[0], DashboardAdapter.PORT_SCAN_TABLE);
        });
    }
}
