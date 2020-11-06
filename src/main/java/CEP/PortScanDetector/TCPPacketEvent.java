package CEP.PortScanDetector;

import org.pcap4j.packet.*;

/**
 * Represent a packet transferred in the network using TCP
 * @author Thien Hoang
 */
public class TCPPacketEvent {
    private String srcAddress;
    private String dstAddress;
    private int srcPort;
    private int dstPort;
    private boolean ack, syn, fin, rst;

    /**
     * Constructor
     * Extract the information from the IP header and the TCP header of the IP packet and bundle them into this POJO
     * Note that in an IP packet, a TCP packet is wrapped inside
     * @param ipHeader IP Header of the packet
     * @param tcpHeader TCP Header of the packet
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
     * Display the packet nicely
     * @return information of the packet
     */
    public String toString() {
        return srcAddress+":"+srcPort+" -> "+dstAddress+":"+dstPort+" "+
                (ack?"ACK ":"--- ")+(syn?"SYN ":"--- ")+(fin?"FIN ":"--- ")+(rst?"RST ":"--- ");
    }

    /**
     * Get IP address of the sender
     * @return IP address of the sender
     */
    public String getSrcAddress() {
        return srcAddress;
    }

    /**
     * Set IP address of the sender
     * @param srcAddress IP address of the sender
     */
    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    /**
     * Get IP address of the receiver
     * @return IP address of the receiver
     */
    public String getDstAddress() {
        return dstAddress;
    }

    /**
     * Set IP address of the receiver
     * @param dstAddress IP address of the receiver
     */
    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    /**
     * Get Port of the sender
     * @return Port of the sender
     */
    public int getSrcPort() {
        return srcPort;
    }

    /**
     * Set Port of the sender
     * @param srcPort Port of the sender
     */
    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    /**
     * Get Port of the receiver
     * @return Port of the receiver
     */
    public int getDstPort() {
        return dstPort;
    }

    /**
     * Set Port of the receiver
     * @param dstPort Port of the receiver
     */
    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    /**
     * Get ACK flag
     * @return true iff ACK=1 in the packet
     */
    public boolean isAck() {
        return ack;
    }

    /**
     * Set ACK flag
     * @param ack true iff ACK=1 in the packet
     */
    public void setAck(boolean ack) {
        this.ack = ack;
    }

    /**
     * Get SYN flag
     * @return true iff SYN=1 in the packet
     */
    public boolean isSyn() {
        return syn;
    }

    /**
     * Set SYN flag
     * @param syn true iff SYN=1 in the packet
     */
    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    /**
     * Get FIN flag
     * @return true iff FIN=1 in the packet
     */
    public boolean isFin() {
        return fin;
    }

    /**
     * Set FIN flag
     * @param fin true iff FIN=1 in the packet
     */
    public void setFin(boolean fin) {
        this.fin = fin;
    }

    /**
     * Get RST flag
     * @return true iff RST=1 in the packet
     */
    public boolean isRst() {
        return rst;
    }

    /**
     * Set RST flag
     * @param rst true iff RST=1 in the packet
     */
    public void setRst(boolean rst) {
        this.rst = rst;
    }
}
