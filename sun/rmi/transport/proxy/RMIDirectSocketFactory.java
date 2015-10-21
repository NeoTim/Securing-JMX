// Written by Teijo Holzer, in August of 2011
//
// Extended by Dhiru to add "com.sun.management.jmxremote.interface" property in October, 2015

package sun.rmi.transport.proxy;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.NetworkInterface;
import java.util.Enumeration;
import java.net.Socket;
import java.net.ServerSocket;
import java.rmi.server.RMISocketFactory;


public class RMIDirectSocketFactory extends RMISocketFactory {

    public Socket createSocket(String host, int port) throws IOException
    {
        return new Socket(host, port);
    }

    public ServerSocket createServerSocket(int port) throws IOException
    {
        // allow JMX to bind to a specific interface
        String jmxremoteInterface = System.getProperty("com.sun.management.jmxremote.interface");
        if (jmxremoteInterface != null) {
            NetworkInterface ni = NetworkInterface.getByName(jmxremoteInterface);
            if (ni != null) {
                Enumeration<InetAddress> e = ni.getInetAddresses();
                if (e.hasMoreElements()) {
                    InetAddress ia = e.nextElement();
                    return new ServerSocket(port, 20, ia);
                }
            }
        }

        // allow JMX to bind to a specific address
        String jmxremoteHost = System.getProperty("com.sun.management.jmxremote.host");
        if (jmxremoteHost != null) {
            InetAddress[] inetAddresses = InetAddress.getAllByName(jmxremoteHost);
            if (inetAddresses.length > 0) {
                return new ServerSocket(port, 20, inetAddresses[0]);
            }
        }

        return new ServerSocket(port);
    }
}
