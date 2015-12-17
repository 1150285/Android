package com.example.rfsc.smartcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

//Comunicação apenas num sentido

class SocketCom extends Thread {

    private Socket client=null;
    private PrintWriter writer=null;
    private String addrIp = "192.168.2.1";
    private int port = 5005;
    DataOutputStream dOut;

    //Properties:
    private final String TAG = "SocketCom Thread";            //Log tag
    private Handler outHandler;                 //handles the OUTgoing msgs
    Bundle myB = new Bundle();                 //used for creating the msgs

    private Handler inHandler;

    @Override
    public void run() {
        //sendMsgToMainThread();  //send to the main activity a msg
        Looper.prepare();

        try {
            Log.i(TAG, "SOCKET OPEN...");
            if (client==null && writer==null) {
                client = new Socket(addrIp, port);
                writer = new PrintWriter(client.getOutputStream());
            }
//            writer.close();
//            client.close();
        } catch (IOException e) {
            e.printStackTrace();
            Log.i(TAG, "ERRO -->" + e.getMessage().toString());
            sendMsgToMainThread("CONERROR");
        }
        inHandler = new Handler() {  //handles the INcoming msgs
            @Override
            public void handleMessage(Message msg) {
                myB = msg.getData();
                Log.i(TAG, "Handler got message " + myB.getString("COMMAND"));

                if (client != null && writer != null) {
                    //dOut = new DataOutputStream(client.getOutputStream());
                    writer.write(myB.getString("COMMAND"));
                    writer.flush();
                    //dOut.writeUTF(myB.getString("COMMAND")+"\n");
                    //dOut.flush();
                }
            }
        };

        Looper.loop();
        //after this line nothing happens because of the LOOP!
        Log.i(TAG, "Lost message");
    }

    //--------------------------
    public SocketCom(Handler mHandler) {
        //C-tor that get a reference object to the MainActivity handler.
        //this is how we know to whom we need to connect with.
        outHandler = mHandler;
    }

    //--------------------------
    public Handler getHandler() {
        //a Get method which return the handler which This Thread is connected with.
        return inHandler;
    }

    //--------------------------
    private void sendMsgToMainThread(String cmd) {
        Message msg = outHandler.obtainMessage();
        myB.putString("COMMAND", cmd);
        msg.setData(myB);
        outHandler.sendMessage(msg);
    }

};

