// dummy program, nothing to see here ;)

import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Enumeration;

public class Main {
    static public void main(String args[]) throws Exception {
        int port = 12345;

        NetworkInterface ni = NetworkInterface.getByName("lo");
        Enumeration e = ni.getInetAddresses();
        if (!e.hasMoreElements())
            return;
        InetAddress ia = (InetAddress) e.nextElement();
        System.out.println(ia);

        ServerSocket ss = new ServerSocket(port, 20, ia);
        System.out.println("Listening on " + port);
        Socket s = ss.accept();
        System.out.println(s);
    }
}
