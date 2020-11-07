package CEP.PortScanDetector;

import org.pcap4j.packet.*;
/**
 * setup TCP packet
 * @author Hoang Van Thien
 */
public class TCPPacketEvent {
    private String srcAddress;
    private String dstAddress;
    private int srcPort;
    private int dstPort;
    private boolean ack, syn, fin, rst;
    /**
     * setup the TCP packet object
     * @param ipHeader instance for ip header in packet
     * @param tcpHeader instance for tcp header in packet
     */
    public TCPPacketEvent(IpPacket.IpHeader ipHeader, TcpPacket.TcpHeader tcpHeader) {
        srcAddress = ipHeader.getSrcAddr().toString();
        dstAddress = ipHeader.getDstAddr().toString();
        srcPort = tcpHeader.getSrcPort().valueAsInt();
        dstPort = tcpHeader.getDstPort().valueAsInt();
        ack = tcpHeader.getAck();
        syn = tcpHeader.getSyn();
        fin = tcpHeader.getFin();
        rst = tcpHeader.getRst();
    }
    /**
     * format the TCP packet's flag to string
     * @return
     */
    public String toString() {
        return srcAddress+":"+srcPort+" -> "+dstAddress+":"+dstPort+" "+
                (ack?"ACK ":"--- ")+(syn?"SYN ":"--- ")+(fin?"FIN ":"--- ")+(rst?"RST ":"--- ");
    }
    /**
     * return the ip address of source device
     * @return the string contain ip address of source device
     */
    public String getSrcAddress() {
        return srcAddress;
    }
    /**
     * set the ip address of source device
     * @param srcAddress instance for source's ip address
     */
    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }
    /**
     * return the ip address of destination device
     * @return the string contain the ip address of destination device
     */
    public String getDstAddress() {
        return dstAddress;
    }
    /**
     * set the ip address of source device
     * @param dstAddress instance for destination's ip address
     */
    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }
    /**
     * return the port number of source device
     * @return the string contain the port number of source device
     */
    public int getSrcPort() {
        return srcPort;
    }
    /**
     * set the port number of source device
     * @param srcPort instance for port number of source device
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }
    /**
     * return the port number of destination device
     * @return the string contain the port number of destination device
     */
    public int getDstPort() {
        return dstPort;
    }
    /**
     * set the port number of destination device
     * @param dstPort instance for port number of destination device
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }
    /**
     * return true if TCP packet's flag is ACK and vice versa
     * @return the boolean value for TCP packet's ACK flag
     */
    public boolean isAck() {
        return ack;
    }
    /**
     * set the object for ACK flag
     * @param ack instance for ACK flag
     */
    public void setAck(boolean ack) {
        this.ack = ack;
    }
    /**
     * return true if TCP packet's flag is SYN and vice versa
     * @return the boolean value for TCP packet's SYN flag
     */
    public boolean isSyn() {
        return syn;
    }
    /**
     * set the object for SYN flag
     * @param syn instance for SYN flag
     */
    public void setSyn(boolean syn) {
        this.syn = syn;
    }
    /**
     * return true if TCP packet's flag is FIN and vice versa
     * @return the boolean value for TCP packet's FIN flag
     */
    public boolean isFin() {
        return fin;
    }
    /**
     * set the object for FIN flag
     * @param fin instance for FIN flag
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }
    /**
     * return true if TCP packet's flag is RST and vice versa
     * @return the boolean value for TCP packet's RST flag
     */
    public boolean isRst() {
        return rst;
    }
    /**
     * set the object for RST flag
     * @param rst instance for RST flag
     */
    public void setRst(boolean rst) {
        this.rst = rst;
    }
}
