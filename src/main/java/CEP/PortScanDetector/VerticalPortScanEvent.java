package CEP.PortScanDetector;

import java.net.*;

public class VerticalPortScanEvent {
    InetAddress hostAddr;

    public VerticalPortScanEvent(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }

    public InetAddress getHostAddr() {
        return hostAddr;
    }
}
