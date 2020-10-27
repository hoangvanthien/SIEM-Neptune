package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

public class HorizontalPortScanEvent {
    Port hostPort;

    public HorizontalPortScanEvent(Port hostPort) {
        this.hostPort = hostPort;
    }

    public Port getHostPort() {
        return hostPort;
    }
}
