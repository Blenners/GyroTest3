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
import android.widget.CompoundButton;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ToggleButton;

public class GyroWorker implements SensorEventListener {
    private Activity mParent;
    private SensorManager mSensorManager;
    private Sensor mRotationSensor;

    private static int[] CurrentValsRPA = new int[3];

    private static int[] OffsetValsRPA = new int[3];

    public static int[] AdjustedValsPRA = new int[3];

    public static int[] LastRunValsPRA = new int[3];

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
                update(truncatedRotationVector);
            } else {
                update(event.values);
            }
        }
    }

    private void update(float[] vectors) {

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

        CurrentValsRPA[0] = (int) pitch + 180;
        CurrentValsRPA[1] = (int) roll + 180;
        CurrentValsRPA[2] = (int) azimuth + 180;


        for (int i = 0; i <=2; i++) {
            AdjustedValsPRA[i] = CurrentValsRPA[i] - OffsetValsRPA[i];
            if (i != 0){
                if (AdjustedValsPRA[i] > 360) {
                    AdjustedValsPRA[i] = AdjustedValsPRA[i] - 360;
                }
                if (AdjustedValsPRA[i] < 0) {
                    AdjustedValsPRA[i] = AdjustedValsPRA[i] + 360;
                }
            }

        }

        ((TextView)mParent.findViewById(R.id.XDisplay)).setText("P: "+ CurrentValsRPA[0]);
        ((TextView)mParent.findViewById(R.id.YDisplay)).setText("R: "+ CurrentValsRPA[1]);
        ((TextView)mParent.findViewById(R.id.ZDisplay)).setText("A: "+ CurrentValsRPA[2]);
        ((TextView)mParent.findViewById(R.id.XAdjusted)).setText("AP: "+(AdjustedValsPRA[0]));
        ((TextView)mParent.findViewById(R.id.YAdjusted)).setText("AR: "+(AdjustedValsPRA[1]));
        ((TextView)mParent.findViewById(R.id.ZAdjusted)).setText("AA: "+(AdjustedValsPRA[2]));
        //if (((IPitch+StartIPitch) > 0) && (LastIPitRead <= 0)){
        //   Toast.makeText( this, "Please look down, max pitch reached", Toast.LENGTH_SHORT).show();
        //}

        LastRunValsPRA[0] = CurrentValsRPA[0]; //P between -90 and 90
        LastRunValsPRA[1] = CurrentValsRPA[1];
        LastRunValsPRA[2] = CurrentValsRPA[2];
        //System.out.println("--------------");
        //System.out.println(AdjustedValsPRA[0]);
    }

    public void ResetPos(){
        OffsetValsRPA[0] = LastRunValsPRA[0];
        OffsetValsRPA[1] = LastRunValsPRA[1];
        OffsetValsRPA[2] = LastRunValsPRA[2];
    }


    public int[] getCurrentVals(boolean buttonState){

        if (buttonState){
            return new int[3]; //TODO check this is all 0
        }
        else{
            return AdjustedValsPRA;
        }

    }
}
