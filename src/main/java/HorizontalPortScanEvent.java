package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

/**
 * setup alert for horizontal port scan
 * @author Lu Minh Khuong
 */
public class HorizontalPortScanEvent {
    Port hostPort;

    /**
     * set host address for scanned device
     * @param hostPort instance for parsing port number
     */
    public HorizontalPortScanEvent(Port hostPort) {
        this.hostPort = hostPort;
    }

    /**
     * return the port number of scanned device
     * @return the string contain port number of host
     */
    public Port getHostPort() {
        return hostPort;
    }
}
