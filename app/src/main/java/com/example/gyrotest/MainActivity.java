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

    long sendTime = 0;
    GyroWorker worker;
    ToggleButton t;
    SocketHandler SH;
    boolean buttonState;


    //TODO best bway to delay
    Runnable r = new Runnable() {
        @Override
        public void run() {

            SH.sendData(worker.getCurrentVals(buttonState));
            try {
                Thread.sleep(500);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        worker = new GyroWorker(this);
        SH = new SocketHandler();
        final Thread sendThread = new Thread(r);

        // Fix or fre mode button
        final ToggleButton t = findViewById(R.id.Lidar);
        t.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {

                if (t.isChecked()) {
                    buttonState = true;
                }
                else{
                    buttonState = false;
                }
            }
        });

        // Data send button
        final ToggleButton s = findViewById(R.id.sendData);
        s.setOnClickListener(new View.OnClickListener(){

            public void onClick(View v) {

                if (s.isChecked()) {
                    //TODO wait 10 secs sothe user can load up the other app
                    worker.ResetPos();
                    sendThread.start();
                }
                else{
                    sendThread.interrupt();
                }
            }
        });

    }


    public void Connect(){
        try {
            SH.connect();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void startDroneFeed(){


    }





