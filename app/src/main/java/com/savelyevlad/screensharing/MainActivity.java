package com.savelyevlad.screensharing;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;

import com.savelyevlad.screensharing.clientPart.MainClient;
import com.savelyevlad.screensharing.serverPart.MainServer;

import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class MainActivity extends AppCompatActivity {

    private Button buttonServer;
    private Button buttonClient;

    // For sending
    private int PORT = 50000;
    private String address = "10.1.1.100";

    private DatagramSocket datagramSocket;

    public int getPORT() {
        return PORT;
    }

    public String getAddress() {
        return address;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener((view) -> Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                .setAction("Action", null).show()
        );

        buttonClient = findViewById(R.id.buttonClient);
        buttonServer = findViewById(R.id.buttonServer);

        buttonServer.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, MainServer.class);
            startActivity(intent);
        });

        buttonClient.setOnClickListener((v) -> {
            Intent intent = new Intent(MainActivity.this, MainClient.class);
            startActivity(intent);
        });

        Initializer initializer = new Initializer();

        try {
            Thread thread = new Thread(initializer);
            thread.setPriority(Thread.MAX_PRIORITY);
            thread.start();
            thread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        datagramSocket = PublicStaticObjects.getDatagramSocket();

        try {
//            PublicStaticObjects.setDatagramSocket(datagramSocket);
            PublicStaticObjects.setInetAddress(InetAddress.getByName(address));
            PublicStaticObjects.setPORT(PORT);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public DatagramSocket getDatagramSocket() {
        return datagramSocket;
    }
}
