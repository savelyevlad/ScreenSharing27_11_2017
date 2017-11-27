package com.savelyevlad.screensharing.clientPart;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.savelyevlad.screensharing.PublicStaticObjects;
import com.savelyevlad.screensharing.R;

import java.io.IOException;
import java.net.DatagramPacket;

import static java.lang.String.valueOf;

public class MainClient extends Activity {

    private ImageView imageView;
    private Button connectButton;
    private Button disconnectButton;
    private EditText editText;

    private boolean started = false;

    public Button getConnectButton() {
        return connectButton;
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.client);

        imageView = findViewById(R.id.imageView);
        connectButton = findViewById(R.id.buttonConnect);
        disconnectButton = findViewById(R.id.buttonDisconnect);
        editText = findViewById(R.id.editText);

        connectButton.setOnClickListener((v) -> {

            DatagramPacket datagramPacket = new DatagramPacket(concat(PublicStaticObjects.getKEY(), new byte[] {-3, Byte.valueOf(valueOf(editText.getText()))}), 4, PublicStaticObjects.getInetAddress(), PublicStaticObjects.getPORT());
            Thread thread = new Thread(() -> {
                try {
                    Log.e("kek", "");
                    PublicStaticObjects.getDatagramSocket().send(datagramPacket);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });

            try {
                thread.start();
                thread.join();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            // TODO: receive

            started = true;
            connectButton.setVisibility(View.INVISIBLE);
            disconnectButton.setVisibility(View.VISIBLE);

            if(started) {
                ClientListener clientListener = new ClientListener(PublicStaticObjects.getInetAddress(), PublicStaticObjects.getPORT(), imageView, connectButton, MainClient.this);
                clientListener.execute();
            }
        });

        disconnectButton.setOnClickListener((v) -> {
            started = false;
            connectButton.setVisibility(View.VISIBLE);
            disconnectButton.setVisibility(View.INVISIBLE);
        });
    }

    private byte[] concat(byte[] a, byte[] b) {
        byte[] t = new byte[a.length + b.length];
        System.arraycopy(a, 0, t, 0, a.length);
        System.arraycopy(b, 0, t, a.length, b.length);
        return t;
    }
}
