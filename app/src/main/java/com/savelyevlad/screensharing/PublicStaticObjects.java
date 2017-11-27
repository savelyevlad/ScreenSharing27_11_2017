package com.savelyevlad.screensharing;

import java.net.DatagramSocket;
import java.net.InetAddress;

public class PublicStaticObjects {

    private static byte[] KEY = new byte[] {14, 88};

    private PublicStaticObjects() {}

    private static PublicStaticObjects instance = new PublicStaticObjects();

    public static PublicStaticObjects getInstance() {
        return instance;
    }

    private static DatagramSocket datagramSocket;

    public static DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }

    public static void setDatagramSocket(DatagramSocket datagramSocket) {
        PublicStaticObjects.datagramSocket = datagramSocket;
    }

    private static InetAddress inetAddress;

    public static InetAddress getInetAddress() {
        return inetAddress;
    }

    public static void setInetAddress(InetAddress inetAddress) {
        PublicStaticObjects.inetAddress = inetAddress;
    }

    private static int PORT;

    public static int getPORT() {
        return PORT;
    }

    public static void setPORT(int PORT) {
        PublicStaticObjects.PORT = PORT;
    }

    public static byte[] getKEY() {
        return KEY;
    }
}
