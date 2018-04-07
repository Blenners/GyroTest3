package com.example.gyrotest;

import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.app.Activity;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity   {

    GyroWorker worker;
    ToggleButton t;
    SocketHandler SH;
    int buttonState = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        worker = new GyroWorker(this);
        t = findViewById(R.id.Lidar);
    }


    public void Connect(){
        try {
            SH.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDroneFeed(){

        worker.ResetPos();
        sendThread s = new sendThread();
        s.start();

    }
    /*
    toggle.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
    public void onCheckedChanged(CompoundButton compoundButton, boolean b){

        if(b){
            buttonState = 0;
        }
        else{
            buttonState = 1;
        }
    }*/

}


