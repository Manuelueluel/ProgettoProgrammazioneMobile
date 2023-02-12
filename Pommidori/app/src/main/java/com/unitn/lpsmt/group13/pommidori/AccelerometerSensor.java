package com.unitn.lpsmt.group13.pommidori;

import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

public class AccelerometerSensor implements SensorEventListener {
    private static final String TAG = "AccelerometerSensor";

    private SensorManager sensorManager;
    private Sensor sensor;
    private float accSample, accEstimated;
    private long currentTimeInMillis, lastTimeMovedInMillis;
    private boolean fiveSecondsLock;
    private static final float THRESHOLD = 0.6F;    //Valore soglia movimento
    private static final float ALPHA = 0.6F;        //Costante media mobile esponenziale ponderata
    private static final long FIVE_SEC_INTERVAL = 5000;
    private static final long START_WINDOW_INTERVAL = 10000; //10 secondi di finestra iniziali per la rilevazione del movimento
    private long startTime;
    private int triggers = 0;
    private static AccelerometerSensor instance = null;

    private AccelerometerSensor( Context context){
        sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener( this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
        startTime = System.currentTimeMillis() + START_WINDOW_INTERVAL;
    }

    public static AccelerometerSensor getInstance( Context context){
        if( instance == null) instance = new AccelerometerSensor(context);
        return instance;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        if( System.currentTimeMillis() > startTime){
            try {
                if (sensorEvent.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
                    float[] mGravity = sensorEvent.values.clone();
                    // Shake detection
                    float x = mGravity[0];
                    float y = mGravity[1];
                    float z = mGravity[2];

                    accSample = (float) (Math.sqrt(x * x + y * y + z * z) - 9.81F);
                    accSample = Math.abs( accSample);
                    //Media mobile esponenziale ponderata
                    accEstimated = (1.0F - ALPHA) * accEstimated + ALPHA * accSample;

                    //Trigger accelerazione oltre valore soglia
                    if( accEstimated > THRESHOLD){
                        currentTimeInMillis = System.currentTimeMillis();

                        //Può triggerare una volta ogni 5 secondi
                        if( !fiveSecondsLock){
                            fiveSecondsLock = true;
                            lastTimeMovedInMillis = currentTimeInMillis;
                            triggers++;

                        }else if( currentTimeInMillis >= lastTimeMovedInMillis + FIVE_SEC_INTERVAL){
                            fiveSecondsLock = false;
                        }
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    public int getTriggers(){
        return triggers;
    }
}
