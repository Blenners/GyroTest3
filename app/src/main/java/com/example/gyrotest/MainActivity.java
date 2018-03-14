package com.example.gyrotest;

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
import android.app.Activity;

public class MainActivity extends Activity implements SensorEventListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
        setContentView(R.layout.activity_main);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        try {
            mSensorManager = (SensorManager) getSystemService(Activity.SENSOR_SERVICE);
            mRotationSensor = mSensorManager.getDefaultSensor(Sensor.TYPE_ROTATION_VECTOR);
            mSensorManager.registerListener(this, mRotationSensor, SENSOR_DELAY);
        } catch (Exception e) {
            Toast.makeText(this, "Hardware compatibility issue", Toast.LENGTH_LONG).show();
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
        ((TextView)findViewById(R.id.XDisplay)).setText("Pitch: "+IPitch);
        ((TextView)findViewById(R.id.YDisplay)).setText("Roll: "+IRoll);
        ((TextView)findViewById(R.id.ZDisplay)).setText("Azimuth: "+IAzi);
        ((TextView)findViewById(R.id.XAdjusted)).setText("Ad Pitch: "+(IPitch+StartIPitch));
        ((TextView)findViewById(R.id.YAdjusted)).setText("Ad Roll: "+(IRoll+StartIRoll));
        ((TextView)findViewById(R.id.ZAdjusted)).setText("Ad Azimuth: "+(IAzi+StartIAzi));
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

    public void ResetPos(View view){
        StartIPitch = -LastIPitRead;
        //StartIAzi = -LastIAzi;
        StartIRoll = -LastIRoll+90;
    }

}


/*
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;
import static java.lang.Math.atan2;
import com.google.vr.sdk.base.HeadTransform;


public class MainActivity extends AppCompatActivity {

    float[] mHeadView = new float[16];
    int [] mXYZ = new int[3];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }

    @Override
    protected void onStart(){
        super.onStart();
        headTransform.getHeadView(mHeadView,0);

        mXYZ[0] = (int) atan2(mHeadView[9], mHeadView[10]);
        mXYZ[1] = (int) atan2(-1*(mHeadView[8]),Math.sqrt(Math.pow(mHeadView[9],2)+Math.pow(mHeadView[10],2)));
        mXYZ[2] = (int) atan2(mHeadView[4],mHeadView[0]);

        TextView xTextView = findViewById(R.id.XDisplay);
        xTextView.setText(Integer.toString(mXYZ[0]));
        TextView yTextView = findViewById(R.id.YDisplay);
        yTextView.setText(Integer.toString(mXYZ[1]));
        TextView zTextView = findViewById(R.id.ZDisplay);
        zTextView.setText(Integer.toString(mXYZ[2]));

    }

}*/
