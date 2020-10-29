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
    private static String fileErrorlog = "/var/log/apache2/error.log";    
    private static String fileAccesslog = "/var/log/apache2/access.log";
    private static ArrayList<String> allLines = new ArrayList<String>();
    private static int currentLine = 0;

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
        while (true) {
            ApacheAccessLogEvent aal = ApacheAccessLogEvent.nextEvent();
            if (aal != null) sendEvent(aal, "AAL_Event");
            NeptuneErrorLogEvent nel = NeptuneErrorLogEvent.nextEvent();
            if (nel != null) sendEvent(nel, "NEL_Event");
        }
    }

    static <EventType> void sendEvent(EventType event, String eventType) {
        EPAdapter.runtime.getEventService().sendEventBean(event, eventType);
    }

}
