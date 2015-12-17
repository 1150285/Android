package com.example.rfsc.smartcar;

import android.app.Activity;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.PowerManager;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

public class Navigation extends Activity implements SensorEventListener {
    private Sensor          accelerometer;
    private SensorManager   sm;
    private TextView        accelaration;
    private float           sensorvalue;
    private String cmd;

    private final   String TAG = "Activity";            //Log tag
    private         SocketCom mThread;                  //spawned thread
    private Bundle  myB = new Bundle();                 //used for creating the msgs
    public          Handler mHandler = new Handler(){   //handles the INcoming msgs

        @Override
        public void handleMessage(Message msg)
        {
            myB = msg.getData();
            if (myB.getString("COMMAND")=="CONERROR"){
                Toast.makeText(getApplicationContext(), "Erro na Ligação - Definições", Toast.LENGTH_LONG).show();
            }else if (myB.getString("COMMAND")=="CONOK"){
                Toast.makeText(getApplicationContext(), "Comunicação OK", Toast.LENGTH_LONG).show();
            }
        }
    };

    void sendMsgToThread(String cmd)
    {
        Message msg = mThread.getHandler().obtainMessage();
        myB.putString("COMMAND", cmd);
        msg.setData(myB);
        mThread.getHandler().sendMessage(msg);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_navigation);

        // Manter ecrã activo
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

       // SocketCom mThread = new SocketCom(NavHandler);
        //Arranca a Thread
        mThread = new SocketCom(mHandler);
        mThread.start();

        sm = (SensorManager) getSystemService(SENSOR_SERVICE); //Sensor Manager
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //Accelerometer Sensor
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // Register o sensor listener  -- implements SensorEventListener
        accelaration = (TextView) findViewById(R.id.accelaration);

        ImageButton btFront = (ImageButton) findViewById(R.id.imgButtonFront);
        btFront.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getApplicationContext(), "FRENTE", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("MOVEFRONT");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "PARAR", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("BREAK");
                }
                return true;
            }
        });

        ImageButton btRear = (ImageButton)findViewById(R.id.imgButtonRear);
        btRear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    Toast.makeText(getApplicationContext(), "TRAS", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("MOVEREAR");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    Toast.makeText(getApplicationContext(), "PARAR", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("BREAK");
                }
                return true;
            }
        });

        Switch switchVideo = (Switch) findViewById(R.id.switchVideo);
        switchVideo.setChecked(true);

        switchVideo.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

                if (isChecked) {
                    Toast.makeText(getApplicationContext(), "VIDEO ON", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("STARTVIDEO");

                } else {
                    Toast.makeText(getApplicationContext(), "VIDEO OFF", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("STOPVIDEO");
                }
            }
        });
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        //sensorvalue = event.values[1];
        //accelaration.setText("Y:" + event.values[1]);

        if (event.values[1] >= -9.8 && event.values[1] < -4.9 && cmd != "MOVELEFT45") {
            Log.d("SENSOR", "MOVELEFT45");
            cmd="MOVELEFT45";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= -4.9 && event.values[1] < -3.3 && cmd != "MOVELEFT30") {
            Log.d("SENSOR", "MOVELEFT30");
            cmd="MOVELEFT30";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= -3.3 && event.values[1] < -1.6 && cmd != "MOVELEFT15") {
            Log.d("SENSOR", "MOVELEFT15");
            cmd="MOVELEFT15";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= -1.6 && event.values[1] < 1.6 && cmd != "MOVESTRAIGHT") {
            Log.d("SENSOR", "MOVESTRAIGHT");
            cmd="MOVESTRAIGHT";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= 1.6 && event.values[1] < 3.3 && cmd != "MOVERIGHT15") {
            Log.d("SENSOR", "MOVERIGHT15");
            cmd="MOVERIGHT15";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= 3.3 && event.values[1] < 4.9 && cmd != "MOVERIGHT30") {
            Log.d("SENSOR", "MOVERIGHT30");
            cmd="MOVERIGHT30";
            sendMsgToThread(cmd);
        } else if (event.values[1] >= 4.9 && event.values[1] < 9.8 && cmd != "MOVERIGHT45") {
            Log.d("SENSOR", "MOVERIGHT45");
            cmd="MOVERIGHT45";
            sendMsgToThread(cmd);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
