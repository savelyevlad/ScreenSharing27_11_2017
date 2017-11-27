package com.savelyevlad.screensharing;

import java.net.DatagramSocket;
import java.net.SocketException;

public class Initializer implements Runnable {

    private DatagramSocket datagramSocket;

    @Override
    public void run() {
        try {
            datagramSocket = new DatagramSocket();
            PublicStaticObjects.setDatagramSocket(datagramSocket);
        } catch (SocketException e) {
            e.printStackTrace();
        }
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}
