package com.unitn.lpsmt.group13.pommidori;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.Log;

public class AccelerometerSensor implements SensorEventListener {
    private static final String TAG = "AccelerometerSensor";

    private SensorManager sensorManager;
    private Sensor sensor;
    private float accSample, accEstimated;
    private long currentTimeInMillis, lastTimeMovedInMillis;
    private boolean fiveSecondsLock;
    private static final float DELTA = 0.6F;
    private static final float ALPHA = 0.6F;
    private static final long FIVE_SEC_INTERVAL = 5000;
    private int triggers = 0;
    private static AccelerometerSensor instance = null;

    private AccelerometerSensor( Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener( this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public static AccelerometerSensor getInstance( Context context){
        if( instance == null) instance = new AccelerometerSensor(context);
        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        try {
            if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                float[] mGravity = sensorEvent.values.clone();
                // Shake detection
                float x = mGravity[0];
                float y = mGravity[1];
                float z = mGravity[2];

                accSample = (float) (Math.sqrt(x * x + y * y + z * z) - 9.81F);
                accSample = Math.abs( accSample);
                accEstimated = (1.0F-ALPHA)*accEstimated + ALPHA*accSample;

//                testo = "accEstimated "+String.format("%.2f", accEstimated)
//                        +"\naccSample "+String.format("%.2f", accSample)
//                        +"\nx="+String.format("%.1f", x)
//                        +"\ny="+String.format("%.1f", y)
//                        +"\nz="+String.format("%.1f",z);

                //Trigger accelerazione oltre soglia delta
                if( accEstimated > DELTA){
                    currentTimeInMillis = System.currentTimeMillis();

                    //Può triggerare una volta ogni 5 secondi
                    if( !fiveSecondsLock){
//                        Log.d("CustomAccelerometer", "Mosso "+testo);
                        fiveSecondsLock = true;
                        lastTimeMovedInMillis = currentTimeInMillis;
                        triggers++;

//                        Intent intent = new Intent();
//                        intent.setAction(MainActivity.CUSTOM_INTENT_ACTION);
//                        intent.putExtra( ACC_TRIGGER, System.currentTimeMillis());
//                        localBroadcastManager.sendBroadcast( intent);
                    }else if( currentTimeInMillis >= lastTimeMovedInMillis + FIVE_SEC_INTERVAL){
                        fiveSecondsLock = false;
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public int getTriggers(){
        return triggers;
    }
}
