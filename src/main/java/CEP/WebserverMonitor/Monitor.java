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

    private static final int snapshotLength = 65536; // in bytes
    private static final int readTimeout = 100; // in milliseconds
    private static final int maxPackets = -1;
    private static final String filter = "tcp";

    public static void main (String [] args) throws Exception {
        System.out.println("Please wait while I'm configuring the Event Processor... ");

        new ApacheAccessLogCEP(10,3);
//        new NeptuneErrorLogCEP(10,3);
        new VerticalPortScan(20, 100);
        new HorizontalPortScan(60, 2, 10); // set to 2 to test, use 5 or more in production

        PcapNetworkInterface device = getNetworkDevice();
        System.out.println(device.getName() + "(" + device.getDescription() + ")");
        System.out.println("You chose: " + device);

        // New code below here
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        final PcapHandle handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        // Set a filter to only listen for tcp packets on port 80 (HTTP)
        if (filter.length() != 0) {
            handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);
        }

        // Tell the handle to loop using the listener we created
        try {
            handle.loop(maxPackets, (PacketListener) packet -> {
                try {
                    IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                    TcpPacket tcpPacket = ipV4Packet.get(TcpPacket.class);
                    int port = tcpPacket.getHeader().getSrcPort().valueAsInt();
                    TCPPacket evt = new TCPPacket(
                            ipV4Packet.getHeader(),
                            tcpPacket.getHeader()
                    );
                    if (port != 443 && port != 80 && port != 62078) {
                        sendEvent(evt, TCPPacket.class.getSimpleName());
                    }
                } catch (Exception ignored) {

                }

                ApacheAccessLogEvent aal = null;
                try {
                    aal = ApacheAccessLogEvent.nextEvent();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                if (aal != null) sendEvent(aal, "AAL_Event");

            });

        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        // Cleanup when complete
        handle.close();

    }

    static <EventType> void sendEvent(EventType event, String eventType) {
        EPAdapter.runtime.getEventService().sendEventBean(event, eventType);
    }

    static PcapNetworkInterface getNetworkDevice() {
        PcapNetworkInterface device = null;
        try {
            device = new NifSelector().selectNetworkInterface();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return device;
    }

}
