package CEP.PortScanDetector;

import java.net.*;

/**
 * setup alert for vertical port scan
 * @author Lu Minh Khuong
 */
public class VerticalPortScanEvent {
    InetAddress hostAddr;

    /**
     * set host address for scanned device
     * @param hostAddr instance for parsing host address
     */
    public VerticalPortScanEvent(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }

    /**
     * return the ip address of scanned device
     * @return the string contain ip address of host
     */
    public InetAddress getHostAddr() {
        return hostAddr;
    }
}
