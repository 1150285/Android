package com.camera.simplemjpeg;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.Toast;

public class MainActivity extends Activity {
    private Boolean vWifi;
    private String wfssid;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.activity_main);



        ImageView im = (ImageView)findViewById(R.id.imgStart);

        im.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                vWifi=  validateWifi(getApplicationContext());
                if (vWifi){
                    Intent i = new Intent(getApplicationContext(), Navigation.class);
                    startActivity(i);
                }
            }
        });
    }

    public boolean validateWifi(Context context) {
        WifiManager manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        if (manager.isWifiEnabled()) {
            WifiInfo wifiInfo = manager.getConnectionInfo();
            if (wifiInfo != null) {
                NetworkInfo.DetailedState state = WifiInfo.getDetailedStateOf(wifiInfo.getSupplicantState());
                if (state == NetworkInfo.DetailedState.CONNECTED || state == NetworkInfo.DetailedState.OBTAINING_IPADDR) {
                    //return wifiInfo.getSSID();
                    return true;
                    //Log.i("WIFI", wifiInfo.getSSID());
                    //wfssid=wifiInfo.getSSID();
                    //Log.i("WIFI2", wfssid);

                   /* if (wifiInfo.getSSID().compareTo("\"SmartCar\"") == 0)
                        return true;
                    else
                        Toast.makeText(getApplicationContext(), "NAO ESTA LIGADO A REDE SMARTCAR", Toast.LENGTH_SHORT).show();
                    */
                }
            }
        }else{
            Toast.makeText(getApplicationContext(), "WIFI DESACTIVADO", Toast.LENGTH_SHORT).show();
            return false;
        }
        return false;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
