package com.example.rfsc.smartcar;

import android.app.Activity;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.TextView;

public class Navigation extends Activity implements SensorEventListener {

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
