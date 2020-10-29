package CEP.PortScanDetector;

import CEP.PortScanDetector.*;
import Utilities.EPAdapter;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.util.*;

import java.io.IOException;
import java.util.*;

/**
 * Setup the Esper's runtime and packet capture, captured network packets are passed to the Esper's runtime
 * @author Lu Minh Khuong
 */
public class Detector {
    private static final int snapshotLength = 65536; // in bytes
    private static final int readTimeout = 100; // in milliseconds
    private static final int maxPackets = -1;
    private static final String filter = "tcp";

    public static void main (String [] args) throws Exception {
        // add your network interface name in args
        String deviceName = args.length > 0 ? args[0] : "";

        System.out.println("Please wait while I'm configuring the Port Scan... ");

        new VerticalPortScanCEP(20, 100);
        new HorizontalPortScanCEP(60, 5, 15); // set to 2 to test, use 5 or more in production
        new BlockPortScanCEP(20,10);

        PcapNetworkInterface device = getNetworkDevice(deviceName);

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
        handle.loop(maxPackets, (PacketListener) packet -> {
            try {
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                TcpPacket tcpPacket = ipV4Packet.get(TcpPacket.class);
                int port = tcpPacket.getHeader().getDstPort().valueAsInt();
                TCPPacket evt = new TCPPacket(
                        ipV4Packet.getHeader(),
                        tcpPacket.getHeader()
                );
                if (port != 443 && port != 80 && port != 62078 && port != 22) {
                    sendEvent(evt, TCPPacket.class.getSimpleName());
                }
            } catch (Exception ignored) {

            }
        });
        // Cleanup when complete
        handle.close();
    }

    static <EventType> void sendEvent(EventType event, String eventType) {
        EPAdapter.runtime.getEventService().sendEventBean(event, eventType);
    }

    /**
     * choose the device for scanning
     * @param deviceName instance to take ip device
     * @return A string contain ip device
     */
    static PcapNetworkInterface getNetworkDevice(String deviceName) {
        PcapNetworkInterface device = null;
        try {
            device = Pcaps.getDevByName(deviceName);
        } catch (PcapNativeException e) {
            e.printStackTrace();
        }
        return device;
    }

}
