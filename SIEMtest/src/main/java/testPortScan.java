import java.io.IOException;
import java.net.*;
import javax.swing.*;

public class testPortScan {
    public static void main(String[] args) {
        InetAddress ip = null;
        String host = null;
        try {

            host=JOptionPane.showInputDialog("Enter the Hostname:\n");
            if(host!=null){
                ip = InetAddress.getByName(host);
                scan(ip); }
        }
        catch (UnknownHostException e) {
            System.err.println(e );
        }

    }

    public static void scan(final InetAddress remote) {

        int port = 0;
        String hostname = remote.getHostName();

        for (port = 0; port < 65536; port++) {
            try {
                Socket s = new Socket(remote, port);
                System.out.println("port is  opening  " + port + " of " + hostname);

            } catch (IOException ex) {
                // The remote host is not listening on this port
                System.out.println("port is closed " + port + " of " + hostname);
            }
        }
    }
    }

