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

    private int LastIPitRead = 0;
    private int LastIAzi = 0;
    private int LastIRoll = 0;

    public int StartIPitch = 0;
    public int StartIAzi = 0;
    public int StartIRoll = 0;

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
        int IAzi = (int) azimuth;
        int IPitch = (int) pitch;
        int IRoll = (int) roll;
        ((TextView)mParent.findViewById(R.id.XDisplay)).setText("Pitch: "+IPitch);
        ((TextView)mParent.findViewById(R.id.YDisplay)).setText("Roll: "+IRoll);
        ((TextView)mParent.findViewById(R.id.ZDisplay)).setText("Azimuth: "+IAzi);
        ((TextView)mParent.findViewById(R.id.XAdjusted)).setText("Ad Pitch: "+(IPitch+StartIPitch));
        ((TextView)mParent.findViewById(R.id.YAdjusted)).setText("Ad Roll: "+(IRoll+StartIRoll));
        ((TextView)mParent.findViewById(R.id.ZAdjusted)).setText("Ad Azimuth: "+(IAzi+StartIAzi));
        //if (((IPitch+StartIPitch) > 0) && (LastIPitRead <= 0)){
        //   Toast.makeText( this, "Please look down, max pitch reached", Toast.LENGTH_SHORT).show();
        //}
        LastIPitRead = IPitch;
        LastIAzi = IAzi;
        LastIRoll = IRoll;
        if (firstRun == 1){
            StartIAzi = IAzi;
        }

    }

    public void ResetPos(){
        StartIPitch = -LastIPitRead;
        //StartIAzi = -LastIAzi;
        StartIRoll = -LastIRoll+90;
    }
}
