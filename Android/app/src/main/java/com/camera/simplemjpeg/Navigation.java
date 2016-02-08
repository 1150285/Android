package com.camera.simplemjpeg;

import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.ImageButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;

import java.io.IOException;
import java.net.URI;

public class Navigation extends Activity implements SensorEventListener {
    //////
    private static final boolean DEBUG = false;
    private static final int REQUEST_SETTINGS = 0;
    final Handler handler = new Handler();
    private final String TAG = "Navigation";            //Log tag
    String URL;
    private Sensor accelerometer, light;
    private SensorManager sm;
    private TextView accelaration, wifiSignal;
    private float sensorvalue;
    private String cmd;
    private SocketCom mThread;                  //spawned thread
    private Bundle BunWifiIn = new Bundle();
    private Bundle BunWifiOut = new Bundle();
    private Bundle BunIn = new Bundle();
    private Bundle BunOut = new Bundle();
    private boolean wifiStat = false;
    private int wifiValue;
    private boolean connstate;
    public Handler mHandler = new Handler() {   //handles the INcoming msgs
        public void handleMessage(Message msg) {
            BunIn = msg.getData();
            if (BunIn.getString("COMMAND") == "CONERROR") {
                //  Toast.makeText(getApplicationContext(), "Erro na Ligação - Definições", Toast.LENGTH_LONG).show();
                Log.i(TAG, "ERRO NA LIGACAO");
                connstate = false;
                Toast.makeText(getApplicationContext(), "ERRO NA COMUNICAÇÃO ", Toast.LENGTH_LONG).show();
                finish();

            } else if (BunIn.getString("COMMAND") == "CONOK") {
                Toast.makeText(getApplicationContext(), "Comunicação OK", Toast.LENGTH_LONG).show();
                connstate = true;
            }
        }
    };
    private MjpegView mv = null;

    ////
    private boolean suspending = false;
    private BroadcastReceiver WifiStateChangedReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {

            int extraWifiState = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);

            switch (extraWifiState) {
                case WifiManager.WIFI_STATE_DISABLING:
                    Log.d("WIFI", "DISABLED");
                    Toast.makeText(getApplicationContext(), "WIFI DESACTIVADO", Toast.LENGTH_SHORT).show();
                    teste();
                    finish();
                    break;
                case WifiManager.WIFI_STATE_DISABLED:
                    Log.d("WIFI", "DISABLED");
                    Toast.makeText(getApplicationContext(), "WIFI DESACTIVADO", Toast.LENGTH_SHORT).show();
                    teste();
                    finish();
                    break;
                case WifiManager.WIFI_STATE_ENABLED:
                    Log.d("WIFI", "ENABLED");
                    WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                    WifiInfo info = wifiManager.getConnectionInfo();
                    Log.d("WIFI", info.getSSID());
                    Log.d("WIFI signal", String.valueOf(info.getRssi()));
                    break;
            }
        }
    };

    //used for creating the msgs
/*
   public Handler wifiThreadHandler = new Handler() {

        public void handleMessage(Message msg) {
            BunWifiIn = msg.getData();
            wifiValue = BunWifiIn.getInt("LINK");
            wifiSignal = (TextView) findViewById(R.id.wifiSignal);
            wifiSignal.setText(" WIFI" );
            if (wifiValue < -70) {
                wifiSignal.setTextColor(Color.parseColor("#FFFF0000"));
                Toast.makeText(getApplicationContext(), "Sinal Wifi muito fraco", Toast.LENGTH_SHORT).show();
            } else {
                if (wifiValue >= (-70) && wifiValue <= (-60)) {
                    wifiSignal.setTextColor(Color.parseColor("#FFFFAA00"));
                    Toast.makeText(getApplicationContext(), "Sinal Wifi fraco ", Toast.LENGTH_SHORT).show();
                } else {
                    if (wifiValue >= (-60) && wifiValue <= (-50)) {
                        wifiSignal.setTextColor(Color.parseColor("#FF00FF1E"));
                    } else {
                        if (wifiValue > (-50)) {
                            wifiSignal.setTextColor(Color.parseColor("#FF00FF1E"));
                        }
                    }
                }
            }
        }
    };
*/
    void sendMsgToThread(String cmd) {
        Message msg = mThread.getHandler().obtainMessage();
        if (cmd != null && connstate == true) {
            BunOut.putString("COMMAND", cmd);
            msg.setData(BunOut);
            mThread.getHandler().sendMessage(msg);
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_navigation);

        ////
        mv = (MjpegView) findViewById(R.id.mv);
        if (mv != null) {
            //mv.setResolution(width, height);
            mv.showFps(true);
            mv.setResolution(640, 480);
            mv.setDisplayMode(MjpegView.SIZE_FULLSCREEN);
        }

        setTitle(R.string.title_connecting);
        //URL = "http://82.89.169.171/axis-cgi/mjpg/video.cgi?camera=&amp;resolution=320x240";
        URL = "http://192.168.1.1:8080/?action=stream";
        new DoRead().execute(URL);

        ////

        this.registerReceiver(this.WifiStateChangedReceiver, new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION));

        // Manter ecrã activo
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);

        // SocketCom mThread = new SocketCom(NavHandler);
        //Arranca a Thread

        mThread = new SocketCom(mHandler);
        mThread.start();

      /* new Thread(new Runnable(wifiThreadHandler) {
            public void run() {
                WifiManager wifiManager = (WifiManager) getSystemService(Context.WIFI_SERVICE);
                int linkSpeed = 0;
                Message msg2 = wifiThreadHandler.obtainMessage();
                Looper.prepare();
                while (true) {
                    linkSpeed = wifiManager.getConnectionInfo().getRssi();
                    Log.i(TAG, "LINK " + linkSpeed);
                    BunWifiOut.putInt("LINK", linkSpeed);
                    msg2.setData(BunWifiOut);
                    wifiThreadHandler.dispatchMessage(msg2);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
            }).start();*/

        sm = (SensorManager) getSystemService(SENSOR_SERVICE); //Sensor Manager
        accelerometer = sm.getDefaultSensor(Sensor.TYPE_ACCELEROMETER); //Accelerometer Sensor
        light = sm.getDefaultSensor(Sensor.TYPE_LIGHT);
        sm.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL); // Register o sensor listener  -- implements SensorEventListener
        sm.registerListener(this, light, SensorManager.SENSOR_DELAY_NORMAL);
        //accelaration = (TextView) findViewById(R.id.accelaration);

        ImageButton btFront = (ImageButton) findViewById(R.id.imgButtonFront);
        btFront.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    //Toast.makeText(getApplicationContext(), "FRENTE", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("MOVEFRONT");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    //Toast.makeText(getApplicationContext(), "PARAR", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("BREAK");
                }
                return true;
            }
        });

        ImageButton btRear = (ImageButton) findViewById(R.id.imgButtonRear);
        btRear.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {

                if (event.getAction() == android.view.MotionEvent.ACTION_DOWN) {
                    //Toast.makeText(getApplicationContext(), "TRAS", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("MOVEREAR");
                } else if (event.getAction() == android.view.MotionEvent.ACTION_UP) {
                    //Toast.makeText(getApplicationContext(), "PARAR", Toast.LENGTH_SHORT).show();
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
                    //Toast.makeText(getApplicationContext(), "VIDEO ON", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("STARTVIDEO");

                } else {
                    //Toast.makeText(getApplicationContext(), "VIDEO OFF", Toast.LENGTH_SHORT).show();
                    sendMsgToThread("STOPVIDEO");
                }
            }
        });
    }

    ///
    public void onResume() {
        if (DEBUG) Log.d(TAG, "onResume()");
        super.onResume();
        if (mv != null) {
            if (suspending) {
                new DoRead().execute(URL);
                suspending = false;
            }
        }

    }

    public void onStart() {
        if (DEBUG) Log.d(TAG, "onStart()");
        super.onStart();
    }

    ///

    @Override
    public void onPause() {

        if (DEBUG) Log.d(TAG, "onPause()");
        super.onPause();
        teste();
        if (mv != null) {
            if (mv.isStreaming()) {
                mv.stopPlayback();
                suspending = true;
            }
        }

    }

    public void onStop() {
        if (DEBUG) Log.d(TAG, "onStop()");
        super.onStop();
    }

    public void onDestroy() {
        if (DEBUG) Log.d(TAG, "onDestroy()");

        if (mv != null) {
            mv.freeCameraMemory();
        }

        super.onDestroy();
    }

    public void setImageError() {
        handler.post(new Runnable() {
            @Override
            public void run() {
                setTitle(R.string.title_imageerror);
                return;
            }
        });
    }

    public void teste() {
        sm.unregisterListener(this, accelerometer);
        sm.unregisterListener(this, light);
        this.unregisterReceiver(this.WifiStateChangedReceiver);
        mThread.setStop();
    }

    @Override
    public void onSensorChanged(SensorEvent event) {

        Sensor sensor = event.sensor;

        if (sensor.getType() == Sensor.TYPE_ACCELEROMETER) {

            if (event.values[1] >= -9.8 && event.values[1] < -2.5 && cmd != "MOVELEFT") {
                Log.d("SENSOR", "MOVELEFT");
                cmd = "MOVELEFT";
                sendMsgToThread(cmd);
            } else if (event.values[1] >= -2.5 && event.values[1] < 2.5 && cmd != "MOVESTRAIGHT") {
                Log.d("SENSOR", "MOVESTRAIGHT");
                cmd = "MOVESTRAIGHT";
                sendMsgToThread(cmd);
            } else if (event.values[1] >= 2.5 && event.values[1] < 9.8 && cmd != "MOVERIGHT") {
                Log.d("SENSOR", "MOVERIGHT");
                cmd = "MOVERIGHT";
                sendMsgToThread(cmd);
            }

        } else if (sensor.getType() == Sensor.TYPE_LIGHT) {

            //accelaration.setText("LUZ:" + event.values[0]);
            /*if (event.values[0] < 25 && cmd != "LIGHTSON") {
                cmd = "LIGHTSON";
                //sendMsgToThread(cmd);
                Toast.makeText(getApplicationContext(), "LIGAR LUZ AUTO", Toast.LENGTH_SHORT).show();
            } else if (cmd != "LIGHTSOFF") {
                cmd = "LIGHTSOFF";
               // sendMsgToThread(cmd);
                Toast.makeText(getApplicationContext(), "DESLIGAR LUZ AUTO", Toast.LENGTH_SHORT).show();
            }*/
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public class DoRead extends AsyncTask<String, Void, MjpegInputStream> {
        protected MjpegInputStream doInBackground(String... url) {
            //TODO: if camera has authentication deal with it and don't just not work
            HttpResponse res = null;
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpParams httpParams = httpclient.getParams();
            HttpConnectionParams.setConnectionTimeout(httpParams, 5 * 1000);
            HttpConnectionParams.setSoTimeout(httpParams, 5 * 1000);
            if (DEBUG) Log.d(TAG, "1. Sending http request");
            try {
                res = httpclient.execute(new HttpGet(URI.create(url[0])));
                if (DEBUG)
                    Log.d(TAG, "2. Request finished, status = " + res.getStatusLine().getStatusCode());
                if (res.getStatusLine().getStatusCode() == 401) {
                    //You must turn off camera User Access Control before this will work
                    return null;
                }
                return new MjpegInputStream(res.getEntity().getContent());
            } catch (ClientProtocolException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-ClientProtocolException", e);
                }
                //Error connecting to camera
            } catch (IOException e) {
                if (DEBUG) {
                    e.printStackTrace();
                    Log.d(TAG, "Request failed-IOException", e);
                }
                //Error connecting to camera
            }
            return null;
        }

        protected void onPostExecute(MjpegInputStream result) {
            mv.setSource(result);
            if (result != null) {
                result.setSkip(1);
                setTitle(R.string.app_name);
            } else {
                setTitle(R.string.title_disconnected);
            }
            mv.setDisplayMode(MjpegView.SIZE_BEST_FIT);
            mv.showFps(false);
        }
    }

    public class RestartApp extends AsyncTask<Void, Void, Void> {
        protected Void doInBackground(Void... v) {
            Navigation.this.finish();
            return null;
        }

        protected void onPostExecute(Void v) {
            startActivity((new Intent(Navigation.this, Navigation.class)));
        }
    }
}

