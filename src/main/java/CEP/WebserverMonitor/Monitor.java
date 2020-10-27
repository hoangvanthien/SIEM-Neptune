package CEP.WebserverMonitor;

import CEP.PortScanDetector.*;
import Utilities.EPAdapter;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.util.*;

import java.io.IOException;
import java.util.ArrayList;

public class Monitor {
    private static String fileErrorlog = "/var/log/apache2/error.log";
    private static String fileAccesslog = "/var/log/apache2/access.log";
    private static ArrayList<String> allLines = new ArrayList<String>();
    private static int currentLine = 0;

    public static void main (String [] args) throws Exception {
        System.out.println("Please wait while I'm configuring the Event Processor... ");

        new ApacheAccessLogCEP(10,3);
//        new NeptuneErrorLogCEP(10,3);
        ApacheAccessLogEvent aal = null;
        try {
            aal = ApacheAccessLogEvent.nextEvent();
        } catch (IOException e) {
            e.printStackTrace();
        }
        if (aal != null) sendEvent(aal, "AAL_Event");
    }

    static <EventType> void sendEvent(EventType event, String eventType) {
        EPAdapter.runtime.getEventService().sendEventBean(event, eventType);
    }

}
