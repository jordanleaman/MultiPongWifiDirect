package com.example.jordan.multipongwifidirect;

import android.content.ContentResolver;
import android.content.Context;
import android.os.AsyncTask;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.nio.charset.Charset;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.nio.charset.Charset;

import android.net.wifi.WpsInfo;
import android.net.wifi.p2p.WifiP2pInfo;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Jordan on 12/5/2016.
 */

public class ClientSendAsyncTask extends AsyncTask {
    private Context context;
    private TextView statusText;
    private WifiP2pInfo info;
    String host;
    int port;
    int len;
    Socket socket = new Socket();
    byte buf[]  = new byte[1024];

    public ClientSendAsyncTask(Context context, View statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            /**
             * Create a client socket with the host,
             * port, and timeout information.
             */
            String text = (String) params[0];
            host = "192.168.1.1";
            port = 8988;
            socket.bind(null);
            socket.connect((new InetSocketAddress(host, port)), 500);

            /**
             * Create a byte stream from a JPEG file and pipe it to the output stream
             * of the socket. This data will be retrieved by the server device.
             */
            OutputStream outputStream = socket.getOutputStream();
            ContentResolver cr = context.getContentResolver();
            outputStream.write(text.getBytes(Charset.forName("UTF-8")));
            outputStream.close();

        } catch (FileNotFoundException e) {
            //catch logic
            return null;
        } catch (IOException e) {
            //catch logic
            return null;
        }

        /**
        * Clean up any open sockets when done
        * transferring or if an exception occurred.
        */
        finally {
            if (socket != null) {
                if (socket.isConnected()) {
                    try {
                        socket.close();
                    } catch (IOException e) {
                        //catch logic
                    }
                }
            }
        }
        return "Success";
    }
}
