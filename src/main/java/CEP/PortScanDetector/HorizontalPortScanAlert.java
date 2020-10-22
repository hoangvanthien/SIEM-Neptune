package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

public class HorizontalPortScanAlert {
    Port hostPort;

    public HorizontalPortScanAlert(Port hostPort) {
        this.hostPort = hostPort;
    }

    public Port getHostPort() {
        return hostPort;
    }
}
