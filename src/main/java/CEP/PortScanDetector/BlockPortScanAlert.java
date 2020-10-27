package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

import java.net.*;

public class BlockPortScanAlert {
    InetAddress hostAddr;
    Port hostPort;
    Long timestamp;

    public BlockPortScanAlert(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }

    public BlockPortScanAlert(Port hostPort) {
        this.hostPort = hostPort;
    }

    public BlockPortScanAlert(Long timestamp) {
        this.timestamp = timestamp;
    }

    public InetAddress getHostAddr() {
        return hostAddr;
    }
    public Port getHostPort() {
        return hostPort;
    }
    public Long getTimestamp() { return timestamp; }

}
