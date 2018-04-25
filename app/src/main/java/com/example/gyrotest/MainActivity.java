package com.example.gyrotest;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.view.View;
import android.app.Activity;
import android.widget.TextView;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity {


    GyroWorker worker; // Listing out attributes
    SocketHandler SH;
    public static boolean buttonState; // Public variable for use in GyroWorker
    public static boolean modeState; // Public variable for use in GyroWorker

    //Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);


    @Override
    protected void onCreate(Bundle savedInstanceState) { // Called when the app is loaded up
        super.onCreate(savedInstanceState);
        worker = new GyroWorker(this); // Creates instances of each class
        SH = new SocketHandler();


        // Fix or free mode button
        final ToggleButton t = findViewById(R.id.Lidar); // Sets up a listener that is called
        t.setOnClickListener(new View.OnClickListener() {//  every time the button is pressed

            public void onClick(View v) {

                if (t.isChecked()) { // Sets the public variable state depending on the state
                    buttonState = true; // of the button
                } else {
                    buttonState = false;
                }
            }
        });

        final ToggleButton t2 = findViewById(R.id.ModeBut); // Sets up a listener that is called
        t2.setOnClickListener(new View.OnClickListener() {//  every time the button is pressed

            public void onClick(View v) {

                if (t2.isChecked()) {
                    worker.ResetPos();
                    modeState = true;
                } else {
                    modeState = false;
                }
            }
        });

        ((TextView) findViewById(R.id.IP_Box)).setText("192.168.0.58");
        ((TextView) findViewById(R.id.PN_Box)).setText("8888");

    }

/*    public void vibrate(){
        if (worker.firstReset){
            // Vibrate for 500 milliseconds
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                v.vibrate(VibrationEffect.createOneShot(500,VibrationEffect.DEFAULT_AMPLITUDE));
            }else{
                //deprecated in API 26
                v.vibrate(500);
            }
        }
    }*/

    public void Connect(View v) { // Called when the connect button is called
        try {                     // (Option in button setting)
            worker.ResetPos();
            SH.connect();         // Calls connect function in SH (SocketHandler) class
        } catch (IOException e) {
            e.printStackTrace();  // Error handling
        }

    }

    public void resetButton(View view){
        //vibrate();
        worker.ResetPos();
    } // Called on "Rest Pos" button press

}







