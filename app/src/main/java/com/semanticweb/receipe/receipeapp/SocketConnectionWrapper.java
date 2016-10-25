package com.semanticweb.receipe.receipeapp;

import android.os.Build;
import android.util.Log;


import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.List;

import static android.content.ContentValues.TAG;

/**
 * Created by Omer on 10/25/2016.
 */

public class SocketConnectionWrapper {

    //webSocket
    static private WebSocketClient mWebSocketClient;

    public SocketConnectionWrapper(){

    }


    /**
     *Maintains connection with websocket and Life Cycle of WebSocket
     */
    private void connectToWebSocket(String urlString){
        URI uri;
        try {
            uri = new URI(urlString);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            return;
        }

        mWebSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake serverHandshake) {
                Log.i("Websocket", "Opened");
                mWebSocketClient.send("Hello from " + Build.MANUFACTURER + " " + Build.MODEL);
            }

            @Override
            public void onMessage(String s) {
                final String message = s;
                //Message is received update UI
            }

            @Override
            public void onClose(int i, String s, boolean b) {
                Log.i("Websocket", "Closed " + s);
            }

            @Override
            public void onError(Exception e) {
                Log.i("Websocket", "Error " + e.getMessage());
            }
        };
        mWebSocketClient.connect();
    }

    /**
     *Helper Methods to interact with WebSocket
     */
    public static void sendMessage(String message){
        mWebSocketClient.send(message);
    }

}
