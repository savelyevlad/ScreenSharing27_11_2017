package com.savelyevlad.screensharing.serverPart;

import com.savelyevlad.screensharing.PublicStaticObjects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.util.LinkedList;
import java.util.Queue;

public class ImageSender implements Runnable {

    public Queue<byte[]> getQueue() {
        return queue;
    }

    private Queue<byte[]> queue = new LinkedList<>();

    private DatagramSocket datagramSocket;
    private int PORT;
    private InetAddress inetAddress;

    public ImageSender(DatagramSocket datagramSocket, String address, int PORT) {
        this.datagramSocket = datagramSocket;
    }

    public ImageSender(DatagramSocket datagramSocket, InetAddress inetAddress, int PORT) {
        this.datagramSocket = datagramSocket;
        this.inetAddress = inetAddress;
        this.PORT = PORT;
    }

    private DatagramPacket packet;

    private byte[] thisKey = concat(PublicStaticObjects.getKEY(), new byte[] { -1 });

    @Override
    public void run() {
        while (true) {
            if(!queue.isEmpty()) {
                byte[] buf = queue.poll();
                buf = concat(thisKey, buf);
                packet = new DatagramPacket(buf, buf.length, inetAddress, PORT);
                try {
                    datagramSocket.send(packet);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }
}
