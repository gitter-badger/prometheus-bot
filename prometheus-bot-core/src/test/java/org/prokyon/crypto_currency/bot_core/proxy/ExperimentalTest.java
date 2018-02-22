package org.prokyon.crypto_currency.bot_core.proxy;

import org.junit.jupiter.api.Test;

import java.net.*;
import java.util.Collections;
import java.util.Enumeration;

import static java.lang.System.out;

public class ExperimentalTest {

    @Test
    void getAllIp() throws UnknownHostException, SocketException {
        Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
        for (NetworkInterface netint : Collections.list(nets))
            displayInterfaceInformation(netint);

        InetAddress[] addr = InetAddress.getAllByName("localhost");
        for (int i = 0; i < addr.length; i++)
            System.out.println(addr[i]);
    }
    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        out.printf("Display name: %s\n", netint.getDisplayName());
        out.printf("Name: %s\n", netint.getName());
        Enumeration<InetAddress> inetAddresses = netint.getInetAddresses();
        for (InetAddress inetAddress : Collections.list(inetAddresses)) {
            if (inetAddress instanceof Inet6Address) {
                // It's ipv6
                out.printf("InetAddress v6: %s\n", inetAddress);
            } else if (inetAddress instanceof Inet4Address) {
                // It's ipv4
                out.printf("InetAddress v4: %s\n", inetAddress);
            }

        }
        out.printf("\n");
    }
}


