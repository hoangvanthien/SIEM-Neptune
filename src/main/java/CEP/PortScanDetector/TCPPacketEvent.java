package CEP.PortScanDetector;

import org.pcap4j.packet.*;

public class TCPPacketEvent {
    private String srcAddress;
    private String dstAddress;
    private int srcPort;
    private int dstPort;
    private boolean ack, syn, fin, rst;

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

    public String toString() {
        return srcAddress+":"+srcPort+" -> "+dstAddress+":"+dstPort+" "+
                (ack?"ACK ":"--- ")+(syn?"SYN ":"--- ")+(fin?"FIN ":"--- ")+(rst?"RST ":"--- ");
    }

    public String getSrcAddress() {
        return srcAddress;
    }

    public void setSrcAddress(String srcAddress) {
        this.srcAddress = srcAddress;
    }

    public String getDstAddress() {
        return dstAddress;
    }

    public void setDstAddress(String dstAddress) {
        this.dstAddress = dstAddress;
    }

    public int getSrcPort() {
        return srcPort;
    }

    public void setSrcPort(int srcPort) {
        this.srcPort = srcPort;
    }

    public int getDstPort() {
        return dstPort;
    }

    public void setDstPort(int dstPort) {
        this.dstPort = dstPort;
    }

    public boolean isAck() {
        return ack;
    }

    public void setAck(boolean ack) {
        this.ack = ack;
    }

    public boolean isSyn() {
        return syn;
    }

    public void setSyn(boolean syn) {
        this.syn = syn;
    }

    public boolean isFin() {
        return fin;
    }

    public void setFin(boolean fin) {
        this.fin = fin;
    }

    public boolean isRst() {
        return rst;
    }

    public void setRst(boolean rst) {
        this.rst = rst;
    }
}
