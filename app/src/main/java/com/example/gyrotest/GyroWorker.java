package com.example.gyrotest;

import android.app.Activity;
import android.content.pm.ActivityInfo;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;
import android.widget.Toast;

public class GyroWorker implements SensorEventListener {
    private Activity mParent;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private int[] LastReadPRA = new int[3];

    private int[] StartValsPRA = new int[3];

    private int[] AdjustedValsPRA = new int[3];

    private static final int SENSOR_DELAY = 500 * 1000; // 500ms
    private static final int FROM_RADS_TO_DEGS = -57;

    public GyroWorker(Activity mParent)
    {
        this.mParent = mParent;
        mParent.setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        mParent.setContentView(R.layout.activity_main);
        mParent.getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            mSensorManager = (SensorManager) mParent.getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(mParent, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // TODO Auto-generated method stub
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor == mRotationSensor) {
            if (event.values.length > 4) {
                float[] truncatedRotationVector = new float[4];
                System.arraycopy(event.values, 0, truncatedRotationVector, 0, 4);
                update(truncatedRotationVector, 0);
            } else {
                update(event.values, 0);
            }
        }
    }

    private void update(float[] vectors, int firstRun) {

        float[] rotationMatrix = new float[9];
        SensorManager.getRotationMatrixFromVector(rotationMatrix, vectors);
        int worldAxisX = SensorManager.AXIS_X;
        int worldAxisZ = SensorManager.AXIS_Z;
        float[] adjustedRotationMatrix = new float[9];
        SensorManager.remapCoordinateSystem(rotationMatrix, worldAxisX, worldAxisZ, adjustedRotationMatrix);
        float[] orientation = new float[3];
        SensorManager.getOrientation(adjustedRotationMatrix, orientation);
        float azimuth = orientation[0] * FROM_RADS_TO_DEGS;
        float pitch = orientation[1] * FROM_RADS_TO_DEGS;
        float roll = orientation[2] * FROM_RADS_TO_DEGS;

        // Add 180 deg to numbers to make them between 0-360 for motor

        LastReadPRA[0] = (int) pitch + 180;
        LastReadPRA[1] = (int) roll + 180;
        LastReadPRA[2] = (int) azimuth + 180;


        for (int i = 0; i <=2; i++){
            if (LastReadPRA[i] < StartValsPRA[i]) {
                AdjustedValsPRA[i] = LastReadPRA[i] - StartValsPRA[i];
            }
            else if (LastReadPRA[i] > StartValsPRA[i]){
                AdjustedValsPRA[i] = 360 - (StartValsPRA[i] - LastReadPRA[i]);
            }
        }

        ((TextView)mParent.findViewById(R.id.XDisplay)).setText("Pitch: "+LastReadPRA[0]);
        ((TextView)mParent.findViewById(R.id.YDisplay)).setText("Roll: "+LastReadPRA[1]);
        ((TextView)mParent.findViewById(R.id.ZDisplay)).setText("Azimuth: "+LastReadPRA[2]);
        ((TextView)mParent.findViewById(R.id.XAdjusted)).setText("Ad Pitch: "+(AdjustedValsPRA[0]));
        ((TextView)mParent.findViewById(R.id.YAdjusted)).setText("Ad Roll: "+(AdjustedValsPRA[1]));
        ((TextView)mParent.findViewById(R.id.ZAdjusted)).setText("Ad Azimuth: "+(AdjustedValsPRA[2]));
        //if (((IPitch+StartIPitch) > 0) && (LastIPitRead <= 0)){
        //   Toast.makeText( this, "Please look down, max pitch reached", Toast.LENGTH_SHORT).show();
        //}

    }

    public void ResetPos(){
        StartValsPRA[0] = LastReadPRA[0];
        StartValsPRA[1] = LastReadPRA[1];
        StartValsPRA[2] = LastReadPRA[2];
    }
}
