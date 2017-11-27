package com.savelyevlad.screensharing.serverPart;

import android.util.Log;

import com.savelyevlad.screensharing.PublicStaticObjects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;

public class IdReceiver implements Runnable {

    private int id;

    private DatagramSocket datagramSocket;

    public IdReceiver(DatagramSocket datagramSocket) {
        this.datagramSocket = datagramSocket;
    }

    @Override
    public void run() {
        try {
//        DatagramSocket datSock = null;
//        datSock = new DatagramSocket(50000);

            datagramSocket = PublicStaticObjects.getDatagramSocket();

//        datagramSocket = datSock;
            DatagramPacket datagramPacket = new DatagramPacket(new byte[]{14, 88, -1}, 3, InetAddress.getByName("192.168.43.106"), 50000);
//        DatagramPacket packet = new DatagramPacket(new byte[] {14, 88, -1}, 3, InetAddress.getByName("192.168.43.106"), 50000);
            Log.e("kek", "33");
            datagramSocket.send(datagramPacket);
            Log.e("kek", "35");
            datagramPacket = new DatagramPacket(new byte[1], 1);
            datagramSocket.receive(datagramPacket);
            id = datagramPacket.getData()[0];
        } catch(Exception ignored) { }
    }

    public int getId() {
        return id;
    }
}
