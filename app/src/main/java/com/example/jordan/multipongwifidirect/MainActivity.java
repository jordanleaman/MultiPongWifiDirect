package com.example.jordan.multipongwifidirect;

import android.content.BroadcastReceiver;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.Uri;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.net.wifi.p2p.nsd.WifiP2pDnsSdServiceInfo;
import android.os.AsyncTask;
import android.os.StrictMode;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Map;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pInfo;

import static android.R.attr.host;
import static com.example.jordan.multipongwifidirect.R.layout.activity_main;
import static com.example.jordan.multipongwifidirect.R.layout.activity_waiting_for_connections;

public class MainActivity extends AppCompatActivity {
    static String TAG = "MainActivity";
    private String[] devices = {"Tablet1", "Phone1", "Phone2", "Tablet2"};
    public TextView mTextView;
    public ListView mListView;
    public String[] myItems = {"red", "blue", "green"};
    private IntentFilter mIntentFilter;
    private ArrayAdapter<String> wifiP2pArrayAdapter;
    private BroadcastReceiver mReceiver;
    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private int server;
    private Context context;
    private final int SERVER_PORT = 8898;
    public WifiP2pConfig config = new WifiP2pConfig();
    final HashMap<String, String> buddies = new HashMap<String, String>();



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (android.os.Build.VERSION.SDK_INT > 9) {
            StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
            StrictMode.setThreadPolicy(policy);
        }
        Log.i(TAG, "Application is running");

        mIntentFilter = new IntentFilter();
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);

        // Indicates a change in the list of available peers.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);

        // Indicates the state of Wi-Fi P2P connectivity has changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);

        // Indicates this device's details have changed.
        mIntentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);

        //populateListView();
        registerClickCallback();

        mTextView = (TextView)findViewById(R.id.mTextView);
        mListView = (ListView) findViewById(R.id.available_devices);

        //ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, R.layout.item, this.devices);
        wifiP2pArrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1);

        mListView.setAdapter(wifiP2pArrayAdapter);

        Button btnScan = (Button) (findViewById(R.id.btnScan));
        btnScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.i(TAG, "Button Clicked");
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                Intent mIntent2 = new Intent(MainActivity.this, WaitingForConnections.class);
                Bundle mBundle = new Bundle();
                mIntent2.putExtras(mBundle);
                String message = "Scanning";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                search(v);
            }
        });

        Button btnHost = (Button) (findViewById(R.id.btnHost));
        btnHost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.i(TAG, "Button Clicked");
                //Intent intent = new Intent(MainActivity.this, SettingsActivity.class);
                //startActivity(intent);
                String message = "Started Hosting";
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                play(v);
            }
        });

        mManager = (WifiP2pManager) getSystemService(Context.WIFI_P2P_SERVICE);
        mChannel = mManager.initialize(this, getMainLooper(), null);
        mReceiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);

    }

    public void search(View view) {
        config.groupOwnerIntent = 15;
        registerReceiver(mReceiver, mIntentFilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                mTextView.setText("Searching...");
            }
            @Override
            public void onFailure(int reasonCode) {
                mTextView.setText("Error: " + reasonCode);
            }
        });
    }

    public void play(View view) {
        server = 1;
        config.groupOwnerIntent = 1;
        registerReceiver(mReceiver, mIntentFilter);
        mManager.discoverPeers(mChannel, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
            }
            @Override
            public void onFailure(int reasonCode) {
                //
            }
        });
        Toast.makeText(getApplicationContext(), "Game will start soon", Toast.LENGTH_LONG).show();
        Intent waitingScreen = new Intent(MainActivity.this, WaitingForConnections.class);
        MainActivity.this.startActivity(waitingScreen);
        AsyncTask result = new FileServerAsyncTask(getApplicationContext(), view.findViewById(R.id.statusText));
        result.execute("test");

    }


    public void displayPeers(WifiP2pDeviceList peerList) {
        wifiP2pArrayAdapter.clear();
        Toast.makeText(getApplicationContext(), "Peers Cleared", Toast.LENGTH_LONG).show();
        for(WifiP2pDevice peer : peerList.getDeviceList()) {
            wifiP2pArrayAdapter.add(peer.deviceAddress);
            Toast.makeText(getApplicationContext(), "Peer Added", Toast.LENGTH_LONG).show();
            mTextView.setText("Search Complete");
        }
    }

    private void registerClickCallback() {
        ListView list = (ListView) findViewById(R.id.available_devices);
        list.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View viewClicked, int position, long id) {
                TextView  textView = (TextView) viewClicked;
                //String message = "You clicked" + textView.getText().toString();
                //Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
                connect(textView.getText().toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(server == 1) {
            registerReceiver(mReceiver, mIntentFilter);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(mReceiver);
    }

    public void connect(final String device) {
        config.groupOwnerIntent = 0;
        config.wps.setup = WpsInfo.PBC;
        config.deviceAddress = device;
        mManager.connect(mChannel, config, new WifiP2pManager.ActionListener() {
            @Override
            public void onSuccess() {
                //
                Toast.makeText(MainActivity.this, "Connected.",
                        Toast.LENGTH_SHORT).show();
                //From here, need to initiate sending a test message.
                //new ClientSendAsyncTask(getApplicationContext(), mTextView.findViewById(R.id.statusText)).execute("test");
                /*
                NetworkInfo networkInfo = (NetworkInfo)intent.getParcelableExtra(extraKey);

                if (networkInfo.isConnected()) {
                    wifiP2pManager.requestConnectionInfo(wifiDirectChannel,
                            new WifiP2pManager.ConnectionInfoListener() {
                                public void onConnectionInfoAvailable(WifiP2pInfo info) {


                                    Toast toast=Toast.makeText(class.this,info.groupOwnerAddress.getHostAddress().toString, Toast.LENGHT_SHORT);
                                    toast.show();

                                }
                            }
                }

                mManager.createGroup(mChannel, new WifiP2pManager.ActionListener() {
                    @Override
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Group Created",
                                Toast.LENGTH_SHORT).show();
                    }

                    @Override
                    public void onFailure(int reason) {
                        Toast.makeText(MainActivity.this, "Could Not Create Group",
                                Toast.LENGTH_SHORT).show();
                    }
                });
                WifiP2pInfo info = new WifiP2pInfo();
                boolean group = info.groupFormed;
                Toast.makeText(MainActivity.this, Boolean.toString(group),
                        Toast.LENGTH_SHORT).show();
                if (info.groupFormed) {
                    // The other device acts as the client. In this case, we enable the
                    // get file button.
                    String clientIP = info.groupOwnerAddress.getHostAddress();
                    Toast.makeText(MainActivity.this, clientIP,
                            Toast.LENGTH_SHORT).show();
                }
                */
            }

            @Override
            public void onFailure(int reason) {
                Toast.makeText(MainActivity.this, "Connect failed. Retry.",
                        Toast.LENGTH_SHORT).show();
            }
        });

    }
    public void start_waiting(String s) {
        Socket socket = new Socket();
        int port = SERVER_PORT;
        String host = s;
        String message = "test";
        Client myClient = new Client(context, s, port);
        myClient.execute();
        Intent clientWaitingScreen = new Intent(MainActivity.this, ClientWaitingActivity.class);
        MainActivity.this.startActivity(clientWaitingScreen);
    }




}