package com.example.gyrotest;


import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.app.Activity;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity {


    GyroWorker worker;
    SocketHandler SH;
    boolean buttonState;
    private Handler mHandler;


    //TODO best way to delay
    Runnable r = new Runnable() {
        @Override
        public void run() {
            try {
                SH.sendData(worker.getCurrentVals(buttonState), SH.printWriter);
                System.out.print(worker.getCurrentVals(buttonState).toString());
                //Thread.sleep(500);
            } /*catch (InterruptedException e) {
                e.printStackTrace();
            }*/ finally {
                mHandler.postDelayed(r, 2000);
                //long l = 1000;
                //  r.wait(l);
            }
        }
    };


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        worker = new GyroWorker(this);
        SH = new SocketHandler();
        final Thread sendThread = new Thread(r);

        mHandler = new Handler();


        // Fix or free mode button
        final ToggleButton t = findViewById(R.id.Lidar);
        t.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (t.isChecked()) {
                    buttonState = true;
                } else {
                    buttonState = false;
                }
            }
        });

        // Data send button
        final ToggleButton s = findViewById(R.id.sendData);
        s.setOnClickListener(new View.OnClickListener() {

            public void onClick(View v) {

                if (s.isChecked()) {
                    s.setBackgroundColor(Color.GREEN);
                    //TODO wait 10 secs so the user can load up the other app
                    worker.ResetPos();
                    sendThread.start();
                } else {
                    s.setBackgroundColor(Color.GRAY);
                    sendThread.interrupt();
                }
            }
        });

    }


    public void Connect(View v) {
        try {
            SH.connect();
        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public void resetButton(View view){
        worker.ResetPos();
    }

}







