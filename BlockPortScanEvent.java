package CEP.PortScanDetector;

import org.pcap4j.packet.namednumber.*;

import java.net.*;
/**
 * setup block port scan object
 * @author Lu Minh Khuong
 */
public class BlockPortScanEvent {
    InetAddress hostAddr;
    Port hostPort;
    Long timestamp;
    /**
     * set host's ip address
     * @param hostAddr instance for host's ip address
     */
    public BlockPortScanEvent(InetAddress hostAddr) {
        this.hostAddr = hostAddr;
    }
    /**
     * set host's port number
     * @param hostPort instance for host's port number
     */
    public BlockPortScanEvent(Port hostPort) {
        this.hostPort = hostPort;
    }
    /**
     * set timestamp when block scanning detect a event
     * @param timestamp instance for detection time point
     */
    public BlockPortScanEvent(Long timestamp) {
        this.timestamp = timestamp;
    }
    /**
     * return the ip address of scanned host
     * @return the string contain the host's ip address
     */
    public InetAddress getHostAddr() {
        return hostAddr;
    }
    /**
     * return the port number of scanned host
     * @return the string contain the host's port number
     */
    public Port getHostPort() {
        return hostPort;
    }
    /**
     * return the timestamp which block scanning occur
     * @return the string contain date/time
     */
    public Long getTimestamp() { return timestamp; }

}
