package CEP.PortScanDetector;

import java.net.*;

public class VerticalPortScanAlert {
    InetAddress hostAddr;

    public VerticalPortScanAlert(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }

    public InetAddress getHostAddr() {
        return hostAddr;
    }
}
