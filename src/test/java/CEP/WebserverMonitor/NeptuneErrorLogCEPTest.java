package CEP.WebserverMonitor;

import Utilities.DashboardAdapter;
import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class NeptuneErrorLogCEPTest {
    @Test
    void test1() throws Exception {
        NeptuneErrorLogCEP.setup();
//        DashboardAdapter.setDisabled(true);
        NeptuneErrorLogEvent event = new NeptuneErrorLogEvent("[Thu Oct 15 23:43:24.866561 2020] [php7:notice] " +
                "[pid 847] [client 192.168.56.1:54578] Neptune: Unauthorized access to /special/code01542.php. " +
                "User has not logged in., referer: http://192.168.56.101/special/");
        EPAdapter.sendEvent(event, "NEL_Event");
    }
}