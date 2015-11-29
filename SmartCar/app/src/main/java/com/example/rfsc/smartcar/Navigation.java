package com.example.rfsc.smartcar;

import android.app.Activity;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;

public class Navigation extends Activity implements SensorEventListener {

    private Socket client;
    private PrintWriter writer;
    private String addrip="192.168.2.1";
    private int port = 9999;

    Sensor accelerometer;
    SensorManager sm;
    TextView accelaration;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        sm=(SensorManager)getSystemService(SENSOR_SERVICE); //Sensor Manager
        accelerometer=sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //Accelerometer Sensor
        //sm.registerListener(this,accelerometer,SensorManager.SENSOR_DELAY_GAME); Alterar para testes
        sm.registerListener(this,accelerometer,1); // Register o sensor listener  -- implements SensorEventListener
        accelaration=(TextView)findViewById(R.id.accelaration);


        Button bt1 = (Button)findViewById(R.id.buttonFront);
        bt1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

             new Thread(new Runnable() {
                 @Override
                 public void run() {
                     try {
                         Log.d("Output","INICIO");
                         client = new Socket(addrip,port);
                         writer = new PrintWriter(client.getOutputStream());
                         writer.write("MOVEFRONT");
                         writer.flush();
                         writer.close();
                         client.close();
                         Log.d("Output","FIM");

                     } catch (IOException e) {
                         e.printStackTrace();
                     }
                 }
             }).start();
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        accelaration.setText("X:"+event.values[0]+
                             "Y:"+event.values[1]+
                             "Z: "+event.values[2]);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }


}
