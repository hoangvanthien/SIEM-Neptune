package CEP.PortScanDetector;

import org.pcap4j.packet.*;

/**
 * setup TCP packet
 * @author Lu Minh Khuong
 */
public class TCPPacket {
    private TcpPacket.TcpHeader tcpHeader;
    private IpPacket.IpHeader ipHeader;

    /**
     * set the ip header and tcp header for packet
     * @param ipHeader instance for ip header
     * @param tcpHeader instance for tcp header
     */
    public TCPPacket(IpPacket.IpHeader ipHeader, TcpPacket.TcpHeader tcpHeader) {
        this.ipHeader = ipHeader;
        this.tcpHeader = tcpHeader;
    }

    /**
     * return the ip address of device
     * @return a string contain ip address
     */
    public IpPacket.IpHeader getIpHeader() {
        return ipHeader;
    }

    /**
     * return the port number of device
     * @return a string contain port number
     */
    public TcpPacket.TcpHeader getTcpHeader() {
        return tcpHeader;
    }
}
