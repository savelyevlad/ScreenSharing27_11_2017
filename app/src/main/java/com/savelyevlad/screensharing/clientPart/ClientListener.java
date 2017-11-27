package com.savelyevlad.screensharing.clientPart;

import android.annotation.SuppressLint;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;

import com.savelyevlad.screensharing.PublicStaticObjects;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;

public class ClientListener extends AsyncTask<Void, Void, Void> {

    private int PORT;
    private InetAddress address;

    @SuppressLint("StaticFieldLeak")
    private ImageView imageView;

    @SuppressLint("StaticFieldLeak")
    private Button connectButton;

    @SuppressLint("StaticFieldLeak")
    private MainClient mainClient;

    public ClientListener(InetAddress address, int PORT, ImageView imageView, Button button, MainClient mainClient) {
        this.address = address;
        this.PORT = PORT;

        this.imageView = imageView;
        this.connectButton = button;
        this.mainClient = mainClient;
    }

    private DatagramSocket datagramSocket;
    private DatagramPacket datagramPacket;

    private byte[] buf = new byte[64 * 1024];

    @Override
    protected Void doInBackground(Void... voids) {
        datagramSocket = PublicStaticObjects.getDatagramSocket();

        mainClient.runOnUiThread(() -> {
            if (mainClient.getConnectButton().getVisibility() == View.INVISIBLE) {
                mainClient.getConnectButton().performClick();
            }
        });

        datagramPacket = new DatagramPacket(buf, buf.length);

        try {
            datagramSocket.receive(datagramPacket);

        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        imageView.setImageBitmap(BitmapFactory.decodeByteArray(datagramPacket.getData(), 0, datagramPacket.getLength()));
        super.onPostExecute(aVoid);
    }
}
