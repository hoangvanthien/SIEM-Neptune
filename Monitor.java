package CEP.WebserverMonitor;

import Dashboard.Dashboard;
import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.io.IOException;
import java.util.ArrayList;
/**
 * setup the monitor to run the CEP of log file
 * @author Hoang Van Thien
 */
public class Monitor {

    public static void main (String [] args) throws Exception {
        execute();
    }
    /**
     * Execute the CEP engine of log file
     * @throws EPCompileException Indicates an exception compiling a module or fire-and-forget query
     * @throws EPDeployException Indicate that a precondition is not satisfied
     * @throws IOException Indicate that failed or interrupted I/O operations
     * @throws NoSuchFieldException Indicate that the method doesn't have a field of a specified name.
     * @throws IllegalAccessException Indicate that the method does not have access to specified field
     */
    public static void execute() throws EPCompileException, EPDeployException, IOException, NoSuchFieldException, IllegalAccessException {
        System.out.println("Please wait while I'm configuring the Event Processor... ");
        ApacheAccessLogCEP.setup();
        NeptuneErrorLogCEP.setup();
        System.out.println("Listening to events...");
        Thread t1 = new Thread(()->{
        while (true) {
            ApacheAccessLogEvent aal = null;
            try {
                aal = ApacheAccessLogEvent.nextEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (aal != null) EPAdapter.sendEvent(aal, "AAL_Event");
            NeptuneErrorLogEvent nel = null;
            try {
                nel = NeptuneErrorLogEvent.nextEvent();
            } catch (IOException e) {
                e.printStackTrace();
            }
            if (nel != null) EPAdapter.sendEvent(nel, "NEL_Event");
        }});
        t1.start();
    }

}
