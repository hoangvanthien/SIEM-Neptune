package CEP.WebserverMonitor;

import Dashboard.Dashboard;
import Utilities.EPAdapter;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;

import java.io.IOException;
import java.util.ArrayList;

public class Monitor {

    public static void main (String [] args) throws Exception {
        execute();
    }

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
