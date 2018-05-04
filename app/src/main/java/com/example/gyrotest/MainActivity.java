package com.example.gyrotest;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.preference.PreferenceManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.app.Activity;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity {


    GyroWorker worker; // Listing out attributes
    SocketHandler SH;
    public static int lidarState; // Public variable for use in GyroWorker
    public static boolean modeState; // Public variable for use in GyroWorker
    public static boolean rollLock = false;

    Vibrator v ;
    SharedPreferences preferences;


    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the app is loaded up
        super.onCreate(savedInstanceState);
        worker = new GyroWorker(this); // Creates instances of each class


         v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);

         preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SH = new SocketHandler(preferences);
        attachEventListener();

        String ipaddress  = preferences.getString("IPADDRESS", "172.20.10.5");
        EditText ipAddressEdit = (EditText)findViewById(R.id.IP_Box);
        ipAddressEdit.setText(ipaddress);

        Integer portNo  = preferences.getInt("PORTNUMBER", 8888);
        EditText portNoEdit = (EditText)findViewById(R.id.PN_Box);
        portNoEdit.setText(Integer.toString(portNo));





        // Fix or free mode button
        final ToggleButton t = findViewById(R.id.Lidar); // Sets up a listener that is called
        t.setOnClickListener(new View.OnClickListener() {//  every time the button is pressed

            public void onClick(View v) {

                if (t.isChecked()) { // Sets the public variable state depending on the state
                    lidarState = 1; // of the button
                } else {
                    lidarState = 0;
                }
            }
        });

        final ToggleButton t2 = findViewById(R.id.ModeBut); // Sets up a listener that is called
        t2.setOnClickListener(new View.OnClickListener() {//  every time the button is pressed

            public void onClick(View v) {

                if (t2.isChecked()) {
                    modeState = true;
                    worker.ResetPos();
                    worker.AdjustedValsPRA[2] = 0;
                } else {
                    modeState = false;
                }
            }
        });

        final ToggleButton t3 = findViewById(R.id.rollLockBut); // Sets up a listener that is called
        t3.setOnClickListener(new View.OnClickListener() {//  every time the button is pressed

            public void onClick(View v) {

                if (t3.isChecked()) {
                    rollLock = true;
                    worker.ResetPos();
                    worker.AdjustedValsPRA[2] = 0;
                } else {
                    rollLock = false;
                }
            }
        });




    }

    public void vibrate(){
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(500);
            }
    }

    public void attachEventListener()
    {

        // IP Address input field
        final EditText ipAddressEdit = (EditText)findViewById(R.id.IP_Box);
        ipAddressEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                String ipaddressnew =  ipAddressEdit.getText().toString();

                SharedPreferences.Editor editor = preferences.edit();
                editor.putString("IPADDRESS",ipaddressnew);
                editor.apply();
            }
        });

        // port number


        final EditText portNumberEdit = (EditText)findViewById(R.id.PN_Box);
        portNumberEdit.addTextChangedListener(new TextWatcher() {
            @Override
            public void afterTextChanged(Editable s) {
                // TODO Auto-generated method stub
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                // TODO Auto-generated method stub
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

                Integer portNumber =  Integer.parseInt(portNumberEdit.getText().toString());
                SharedPreferences.Editor editor = preferences.edit();
                editor.putInt("PORTNUMBER",  portNumber);
                editor.apply();
            }
        });




    }
/*    public  EditText waitBox = (EditText)findViewById(R.id.waitTime);
    String editTextStr = waitBox.getText().toString();
    public  Thread conthread = new Thread(new Runnable() { // Creates this as a thread

        @Override
        public void run() {
            if (commReset == false) {
                int waitTime = Integer.parseInt(editTextStr);
                try {
                    conthread.sleep((waitTime) * 1000);
                    worker.ResetPos();
                    commReset = true;
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            else {
                return;
            }

        }

    });*/

    //final Button b = findViewById(R.id.ConnectButton);
    public void Connect(View v){ // Called when the connect button is called
        try {                     // (Option in button setting)
            //b.setBackgroundColor(Color.GREEN);
            worker.ResetPos();
            //conthread.start();
            SH.connect();         // Calls connect function in SH (SocketHandler) class
        } catch (IOException e) {
            e.printStackTrace();  // Error handling
        }

    }

    public void resetButton(View view){
        vibrate();
        worker.ResetPos();

    } // Called on "Rest Pos" button press

}







