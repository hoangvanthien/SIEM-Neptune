package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

import java.net.*;

public class BlockPortScanEvent {
    InetAddress hostAddr;
    Port hostPort;
    Long timestamp;

    public BlockPortScanEvent(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }

    public BlockPortScanEvent(Port hostPort) {
        this.hostPort = hostPort;
    }

    public BlockPortScanEvent(Long timestamp) {
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
