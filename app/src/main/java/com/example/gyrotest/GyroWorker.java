package com.example.gyrotest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

import static java.lang.Math.abs;

public class GyroWorker implements SensorEventListener {
    private Activity mParent;               // Main function, calls this class
    private SensorManager mSensorManager;   // Creates instance of SensorManager
    private Sensor mRotationSensor;         // Creates instance of Sensor

    //Arrays used for storing all needed data values
    private static int[] CurrentValsRPA = new int[3];

    private static int[] OffsetValsRPA = new int[3];

    public static int[] AdjustedValsPRA = new int[3];

    public static int[] LastRunValsPRA = new int[3];

    public static int[] LimitedValsPRA = new int[3];

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms - sampling rate or sensors
    private static final int FROM_RADS_TO_DEGS = -57;   // Constant to convert Radians to degrees

    private static long lastUpdate = 0;

    public GyroWorker(Activity mParent)
    {
        this.mParent = mParent;
        mParent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE); // Forces the app to be landscape
        mParent.setContentView(R.layout.activity_main);
        mParent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            // Allocates classes to variables
            mSensorManager = (SensorManager) mParent.getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            // Message shows in case that the phone doesn't have the desired hardware
            Toast.makeText(mParent, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override  // Called when The read values on the sensors change
    public void onSensorChanged(SensorEvent event) { // Gets rotation vectors
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector);
            } else {
                update(event.values); // Updates the UI and Arrays
            }
        }
    }

    private void update(float[] vectors) { // Called every time the sensor values change,
                                           // from the function

        float[] rotationMatrix = new float[9];
        // Converts rotation vectors into a rotation matrix
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation); // Finds the orientation
        float azimuth = orientation[0] * FROM_RADS_TO_DEGS; // Converts the output for each axis
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;   // from radians to degrees
        float roll = orientation[2] * FROM_RADS_TO_DEGS;

        // Add 180 deg to numbers to make them between 0-360 for motor

        CurrentValsRPA[0] = (int) pitch + 180;
        CurrentValsRPA[1] = (int) roll + 180;
        CurrentValsRPA[2] = (int) azimuth + 180;

        // 30* upwards,90* downwards tilt. ±45*roll. ±90* yaw - gimbal restrictions

        // Checks to see if the camera is in fixed mode for the lidar
        if (MainActivity.buttonState == false){
            if(MainActivity.modeState == false){
                for (int i = 0; i <=2; i++) { // Adjusts the values to be centered around the start point
                    AdjustedValsPRA[i] = CurrentValsRPA[i] - OffsetValsRPA[i];
                    if (i != 0) {
                        if (AdjustedValsPRA[i] > 360) {
                            AdjustedValsPRA[i] = AdjustedValsPRA[i] - 360;
                        }
                        if (AdjustedValsPRA[i] < 0) {
                            AdjustedValsPRA[i] = AdjustedValsPRA[i] + 360;
                        }
                    }
                }
            }
            else if ((lastUpdate - System.currentTimeMillis()) >= 500) {
                    AdjustedValsPRA[0] = CurrentValsRPA[0] - OffsetValsRPA[0];
                    AdjustedValsPRA[1] = 0;
                    int currentPos = abs(CurrentValsRPA[1] - 270);
                    if (currentPos <= 10){
                        //System.currentTimeMillis();
                    }
                    else if (currentPos <= 20){
                        AdjustedValsPRA[2] = AdjustedValsPRA[2] + 10;
                        lastUpdate = System.currentTimeMillis();
                    }
                    else{
                        AdjustedValsPRA[2] = AdjustedValsPRA[2] + 10;
                        lastUpdate = System.currentTimeMillis();
                    }
            }
        }
        else {
            for (int i = 0; i <= 2; i++) {
                AdjustedValsPRA[i] = 0;
            }
        }

        // Updates the UI
        ((TextView)mParent.findViewById(R.id.XDisplay)).setText("P: "+ CurrentValsRPA[0]);
        ((TextView)mParent.findViewById(R.id.YDisplay)).setText("R: "+ CurrentValsRPA[1]);
        ((TextView)mParent.findViewById(R.id.ZDisplay)).setText("A: "+ CurrentValsRPA[2]);
        ((TextView)mParent.findViewById(R.id.XAdjusted)).setText("AP: "+(AdjustedValsPRA[0]));
        ((TextView)mParent.findViewById(R.id.YAdjusted)).setText("AR: "+(AdjustedValsPRA[1]));
        ((TextView)mParent.findViewById(R.id.ZAdjusted)).setText("AA: "+(AdjustedValsPRA[2]));
        //if (((IPitch+StartIPitch) > 0) && (LastIPitRead <= 0)){
        //   Toast.makeText( this, "Please look down, max pitch reached", Toast.LENGTH_SHORT).show();
        //}

        LastRunValsPRA[0] = CurrentValsRPA[0]; // P between -90 and 90
        LastRunValsPRA[1] = CurrentValsRPA[1]; // Updates Last Run values so there is always a
        LastRunValsPRA[2] = CurrentValsRPA[2]; // variable to be read in the SocketHandler Class

        // 30* upwards,90* downwards tilt. ±45*roll. ±90* yaw - gimbal restrictions
        if ((LastRunValsPRA[0] <= 30) && (LastRunValsPRA[0] >= -90)){
            LimitedValsPRA[0] = LastRunValsPRA[0];
        }
        if (abs(LastRunValsPRA[1]) <= 45){
            LimitedValsPRA[1] = LastRunValsPRA[1];
        }
        if (abs(LastRunValsPRA[2]) <= 90){
            LimitedValsPRA[2] = LastRunValsPRA[1];
        }


    }

    public void ResetPos(){ // Re-centres the start point so that it is looking forwards
        OffsetValsRPA[0] = LastRunValsPRA[0];
        OffsetValsRPA[1] = LastRunValsPRA[1];
        OffsetValsRPA[2] = LastRunValsPRA[2];
    }

}
