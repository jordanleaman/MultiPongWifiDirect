package com.example.jordan.multipongwifidirect;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.commons.io.IOUtils;

import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.net.ServerSocket;
import java.net.Socket;

/**
 * Created by Jordan on 12/5/2016.
 */

public class FileServerAsyncTask extends AsyncTask {
    private Context context;
    private TextView statusText;
    private String testresult;

    public FileServerAsyncTask(Context context, View statusText) {
        this.context = context;
        this.statusText = (TextView) statusText;
    }
    @Override
    protected Object doInBackground(Object[] params) {
        try {
            String message = (String) params[0];
            /**
             * Create a server socket and wait for client connections. This
             * call blocks until a connection is accepted from a client
             */
            ServerSocket serverSocket = new ServerSocket(8898);
            Socket client = serverSocket.accept();

            /**
             * If this code is reached, a client has connected and transferred data
             * Save the input stream from the client as a JPEG file
             */

            InputStream inputstream = client.getInputStream();
            StringWriter writer = new StringWriter();
            IOUtils.copy(inputstream, writer);
            String theString = writer.toString();
            //String theString = "message received!";
            serverSocket.close();
            return theString;
        } catch (IOException e) {
            Log.e(MainActivity.TAG, e.getMessage());
            return null;
        }
    }

    /**
     * Start activity that can handle the JPEG image
     */
    protected void onPostExecute(String result) {
        if (result != null) {
            Toast.makeText(context, "something received!", Toast.LENGTH_LONG).show();
            statusText.setText("File copied - " + result);
            Intent intent = new Intent();
            intent.setAction(android.content.Intent.ACTION_VIEW);
            //intent.setDataAndType(Uri.parse("file://" + result), "image/*");
            context.startActivity(intent);
        }
        else {
            Toast.makeText(context, "result was null", Toast.LENGTH_LONG).show();
        }
    }

    public String get_value() {
        return testresult;
    }

}

