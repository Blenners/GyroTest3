package com.example.gyrotest;

import android.os.Bundle;
import android.view.View;
import android.app.Activity;
import android.widget.ToggleButton;

import java.io.IOException;

public class MainActivity extends Activity {


    GyroWorker worker; // Listing out attributes
    SocketHandler SH;
    public static boolean buttonState; // Public variable for use in GyroWorker
    public static boolean modeState; // Public variable for use in GyroWorker


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

    }


    public void Connect(View v) { // Called when the connect button is called
        try {                     // (Option in button setting)
            worker.ResetPos();
            SH.connect();         // Calls connect function in SH (SocketHandler) class
        } catch (IOException e) {
            e.printStackTrace();  // Error handling
        }

    }

    public void resetButton(View view){
        worker.ResetPos();
    } // Called on "Rest Pos" button press

}







