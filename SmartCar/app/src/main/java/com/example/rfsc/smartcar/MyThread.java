package com.example.rfsc.smartcar;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.util.Log;

/**
 * Created by bmgomes on 12/12/2015.
 */
public class MyThread extends Thread{
    //Properties:
    private final   String TAG = "MyThread";            //Log tag
    private Handler outHandler;                 //handles the OUTgoing msgs
    Bundle myB = new Bundle();                 //used for creating the msgs
    private         Handler inHandler = new Handler(){  //handles the INcoming msgs
        @Override public void handleMessage(Message msg)
        {
            myB = msg.getData();
            Log.i(TAG, "Handler got message" + myB.getInt("MAIN DELIVERY"));
        }
    };

    //Methods:
    //--------------------------
    public void run(){
        sendMsgToMainThread();  //send to the main activity a msg
        Looper.prepare();
        Looper.loop();
        //after this line nothing happens because of the LOOP!
        Log.i(TAG, "Lost message");
    }
    //--------------------------
    public MyThread(Handler mHandler) {
        //C-tor that get a reference object to the MainActivity handler.
        //this is how we know to whom we need to connect with.
        outHandler = mHandler;
    }
    //--------------------------
    public Handler getHandler(){
        //a Get method which return the handler which This Thread is connected with.
        return inHandler;
    }
    //--------------------------
    private void sendMsgToMainThread(){
        Message msg = outHandler.obtainMessage();
        myB.putInt("THREAD DELIVERY", 123);
        msg.setData(myB);
        outHandler.sendMessage(msg);
    }
}
