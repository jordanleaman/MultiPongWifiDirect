package com.example.jordan.multipongwifidirect;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pConfig;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pInfo;
import android.net.wifi.p2p.WifiP2pManager;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class WifiDirectBroadcastReceiver extends BroadcastReceiver {

    private WifiP2pManager mManager;
    private WifiP2pManager.Channel mChannel;
    private MainActivity mActivity;
    private List<WifiP2pDevice> mPeers;
    private List<WifiP2pConfig> mConfigs;
    private List<String> connectedDevices;


    public WifiDirectBroadcastReceiver(WifiP2pManager manager, WifiP2pManager.Channel channel, MainActivity activity) {
        super();
        this.mManager = manager;
        this.mChannel = channel;
        this.mActivity = activity;

    }
    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
            // Determine if Wifi P2P mode is enabled or not, alert
            // the Activity.
            int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
            if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                mActivity.mTextView.setText("Enabled");
            } else {
                mActivity.mTextView.setText("Disabled");
            }
        } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

            mPeers = new ArrayList<WifiP2pDevice>();
            mConfigs = new ArrayList<WifiP2pConfig>();

            if (mManager != null) {
                WifiP2pManager.PeerListListener peerListListener = new WifiP2pManager.PeerListListener(){
                    @Override
                    public void onPeersAvailable(WifiP2pDeviceList peerList) {
                        mPeers.clear();
                        mPeers.addAll(peerList.getDeviceList());
                        mActivity.displayPeers(peerList);
                        mPeers.addAll(peerList.getDeviceList());

                        for(int i = 0; i < peerList.getDeviceList().size(); i++) {
                            WifiP2pConfig config = new WifiP2pConfig();
                            config.deviceAddress = mPeers.get(i).deviceAddress;
                            mConfigs.add(config);
                        }
                    }
                };

                mManager.requestPeers(mChannel, peerListListener);
            }

        } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

            // Connection state changed!  We should probably do something about
            // that.

            if (mManager == null) {
                return;
            }
            final NetworkInfo networkInfo = (NetworkInfo) intent
                    .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);


            if (networkInfo.isConnected()) {

                mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {

                    @Override
                    public void onConnectionInfoAvailable(WifiP2pInfo info) {

                        InetAddress groupOwnerAddress = info.groupOwnerAddress;
                        String s=groupOwnerAddress.getHostAddress();
                        Toast.makeText(mActivity, "Server IP Address "+s, Toast.LENGTH_SHORT).show();
                        //connectedDevices.add(s);
                        Boolean owner = info.isGroupOwner;
                        if(owner == true) {
                            Toast.makeText(mActivity, "Group Owner", Toast.LENGTH_SHORT).show();
                        }
                        if(owner == false) {
                            Toast.makeText(mActivity, "Peer"+s, Toast.LENGTH_SHORT).show();
                            mActivity.start_waiting(s);
                        }

                    }
                });
            }



        } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
            //
        }
    }
    public List<String> get_connected_peers() {
        return connectedDevices;
    }



}