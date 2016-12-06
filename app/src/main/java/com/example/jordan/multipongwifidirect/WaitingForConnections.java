package com.example.jordan.multipongwifidirect;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

import java.security.AccessControlContext;

public class WaitingForConnections extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_waiting_for_connections);

    }

    protected void updatePeers(String peer) {
        //peersArrayAdapter.add(peer);
        Toast.makeText(WaitingForConnections.this, "Peer Added", Toast.LENGTH_LONG).show();
    }

    protected void onDestroy() {

    }

}
