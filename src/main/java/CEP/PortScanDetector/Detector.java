package CEP.PortScanDetector;

import Utilities.EPAdapter;
import com.espertech.esper.common.client.module.ParseException;
import com.espertech.esper.compiler.client.EPCompileException;
import com.espertech.esper.runtime.client.EPDeployException;
import org.pcap4j.core.*;
import org.pcap4j.packet.*;
import org.pcap4j.util.*;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.*;

/**
 * main class of the Port Scan Detector component
 * @author Khuong Lu, Thien Hoang
 */
public class Detector {
    private static final int snapshotLength = 65536; // in bytes
    private static final int readTimeout = 100; // in milliseconds
    private static final int maxPackets = -1;
    private static final String filter = "tcp";
    private static Thread t1 = null;

    /**
     * main function if you want to run the Port Scan Detector alone
     * @param args command line arguments
     * @throws EPCompileException
     * @throws IOException
     * @throws EPDeployException
     * @throws PcapNativeException
     * @throws InterruptedException
     * @throws NotOpenException
     * @throws ParseException
     */
    public static void main (String [] args) throws EPCompileException, IOException, EPDeployException, PcapNativeException, InterruptedException, NotOpenException, ParseException {
        execute();
    }

    /**
     * Run the Port Scan Detector
     * @throws EPCompileException
     * @throws IOException
     * @throws EPDeployException
     * @throws PcapNativeException
     * @throws NotOpenException
     * @throws InterruptedException
     * @throws ParseException
     */
    public static void execute() throws EPCompileException, IOException, EPDeployException, PcapNativeException, NotOpenException, InterruptedException, ParseException {
        String deviceName = "any";
        PcapNetworkInterface device = getNetworkDevice(deviceName);

        System.out.println("Please wait while I'm configuring the Port Scan... ");
        SinglePortScanCEP.setup();
        VerticalPortScanCEP.setup();
        HorizontalPortScanCEP.setup();
        // New code below here
        if (device == null) {
            System.out.println("No device chosen.");
            System.exit(1);
        }

        final PcapHandle handle = device.openLive(snapshotLength, PcapNetworkInterface.PromiscuousMode.PROMISCUOUS, readTimeout);

        // Set a filter to only listen for tcp packets on port 80 (HTTP)
        handle.setFilter(filter, BpfProgram.BpfCompileMode.OPTIMIZE);

        // Tell the handle to loop using the listener we created
        handle.loop(maxPackets, (PacketListener) packet -> {
            try {
                IpV4Packet ipV4Packet = packet.get(IpV4Packet.class);
                TcpPacket tcpPacket = ipV4Packet.get(TcpPacket.class);
                TCPPacketEvent evt = new TCPPacketEvent(ipV4Packet.getHeader(), tcpPacket.getHeader());
                if (evt.getSrcAddress().equals(evt.getDstAddress())) return;
//                System.out.println(evt.toString());
                sendEvent(evt, "TCPPacket_Event");
            } catch (Exception ignored) {

            }
        });
        // Cleanup when complete
        handle.close();
    }

    static <EventType> void sendEvent(EventType event, String eventType) {
        EPAdapter.runtime.getEventService().sendEventBean(event, eventType);
    }

    static PcapNetworkInterface getNetworkDevice(String deviceName) {
        try {
            return Pcaps.getDevByName(deviceName);
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return null;
        }
    }

    static PcapNetworkInterface getNetworkDevice(InetAddress inetAddress) {
        try {
            return Pcaps.getDevByAddress(inetAddress);
        } catch (PcapNativeException e) {
            e.printStackTrace();
            return null;
        }
    }

}
