package com.unitn.lpsmt.group13.pommidori.activities;

import static com.unitn.lpsmt.group13.pommidori.Utility.END_OF_PAUSA_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER_PRECEDENTE;
import static com.unitn.lpsmt.group13.pommidori.Utility.TIMER_ACTION_INTENT;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentTransaction;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.util.Log;
import android.view.View;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.broadcastReceivers.EndOfPausaTimerBroadcastReceiver;
import com.unitn.lpsmt.group13.pommidori.broadcastReceivers.TimerBroadcastReceiver;
import com.unitn.lpsmt.group13.pommidori.fragments.CountDownTimerFragment;
import com.unitn.lpsmt.group13.pommidori.fragments.CountUpTimerFragment;
import com.unitn.lpsmt.group13.pommidori.fragments.PausaTimerFragment;
import com.unitn.lpsmt.group13.pommidori.broadcastReceivers.ToolbarAndButtonsBroadcastReceiver;
import com.unitn.lpsmt.group13.pommidori.services.CountDownTimerService;
import com.unitn.lpsmt.group13.pommidori.services.CountUpTimerService;
import com.unitn.lpsmt.group13.pommidori.services.PausaTimerService;

public class Timer extends AppCompatActivity implements TimerBroadcastReceiver.UpdateTimer,
        ToolbarAndButtonsBroadcastReceiver.UpdateToolbarAndButtons, CountDownTimerFragment.SetToolbar,
        CountDownTimerFragment.SwitchFragment, PausaTimerFragment.SwitchFragment,
        CountUpTimerFragment.SwitchFragment, EndOfPausaTimerBroadcastReceiver.SwitchFragment{

    private static final String TAG = "Timer";

    //Variabili
    private Toolbar toolbar;
    private LocalBroadcastManager localBroadcastManager;
    private TimerBroadcastReceiver timerBroadcastReceiver;
    private ToolbarAndButtonsBroadcastReceiver toolbarBroadcastReceiver;
    private EndOfPausaTimerBroadcastReceiver endOfPausaTimerBroadcastReceiver;
    private SharedPreferences sharedPreferences;
    private StatoTimer statoTimer;
    private StatoTimer statoTimerPrecedente;
    private CountDownTimerFragment countDownTimerFragment;
    private CountUpTimerFragment countUpTimerFragment;
    private PausaTimerFragment pausaTimerFragment;
    private CountDownTimerService countDownService;
    private CountUpTimerService countUpService;
    private PausaTimerService pausaService;
    private boolean countDownBounded = false;
    private boolean countUpBounded = false;
    private boolean pausaBounded = false;

    //Connessione al CountDownTimerService, permette il bounding e l'aggiornamento del timer all'immediata apertura del fragment
    private ServiceConnection countDownServiceConnection = new ServiceConnection(){

        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CountDownTimerService.CountDownTimerBinder binder = (CountDownTimerService.CountDownTimerBinder) iBinder;
            countDownService = binder.getService( getBaseContext());
            countDownBounded = true;
            updateTimer( countDownService.getTempoRimasto());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            countDownBounded = false;
        }
    };

    //Connessione al CountupTimerService, permette il bounding e l'aggiornamento del timer all'immediata apertura del fragment
    private ServiceConnection countUpServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            CountUpTimerService.CountUpTimerBinder binder = (CountUpTimerService.CountUpTimerBinder) iBinder;
            countUpService = binder.getService( getBaseContext());
            countUpBounded = true;
            updateTimer( countUpService.getTempoTrascorso());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            countUpBounded = false;
        }
    };

    //Connessione al PausaTimerService, permette il bounding e l'aggiornamento del timer all'immediata apertura del fragment
    private ServiceConnection pausaServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            PausaTimerService.PausaTimerBinder binder = (PausaTimerService.PausaTimerBinder) iBinder;
            pausaService = binder.getService( getBaseContext());
            pausaBounded = true;
            updateTimer( pausaService.getTempoRimasto());
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            pausaBounded = false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);
        Log.d(TAG, "onCreate");

        //Inizializzazione variabili
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        statoTimer = new StatoTimer();
        statoTimerPrecedente = new StatoTimer();
        toolbar = findViewById(R.id.timerToolbar);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveSharedPreferences();
                startActivity(new Intent(Timer.this, Homepage.class));
            }
        });

        //Metodi
        loadSharedPreferences();
        setFragmentAndService();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "onStart");
        //Registrazione Broadcasts
        localBroadcastManager = LocalBroadcastManager.getInstance( this);
        timerBroadcastReceiver = new TimerBroadcastReceiver( this);
        IntentFilter intentFilterTimer = new IntentFilter();
        intentFilterTimer.addAction(TIMER_ACTION_INTENT);
        localBroadcastManager.registerReceiver( timerBroadcastReceiver, intentFilterTimer);

        toolbarBroadcastReceiver = new ToolbarAndButtonsBroadcastReceiver( this);
        IntentFilter intentFilterToolbar = new IntentFilter();
        intentFilterToolbar.addAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        localBroadcastManager.registerReceiver( toolbarBroadcastReceiver, intentFilterToolbar);

        endOfPausaTimerBroadcastReceiver = new EndOfPausaTimerBroadcastReceiver( this);
        IntentFilter intentFilterEndPausa = new IntentFilter();
        intentFilterEndPausa.addAction(END_OF_PAUSA_INTENT);
        localBroadcastManager.registerReceiver( endOfPausaTimerBroadcastReceiver, intentFilterEndPausa);

        //Aggiorna il timer al riaccesso all'app
        if( countDownBounded){
            updateTimer( countDownService.getTempoRimasto());
        }
        if( countUpBounded){
            updateTimer( countUpService.getTempoTrascorso());
        }
        if( pausaBounded){
            updateTimer( pausaService.getTempoRimasto());
        }

        //Aggiorna l'UI al riaccesso all'app
        if( statoTimer.isDisattivo()){
            updateToolbarAndButtons( R.string.pomodoro_disattivo);

        }else if( statoTimer.isCountDown() || statoTimer.isCountUp()){
            updateToolbarAndButtons( R.string.pomodoro_in_corso);

        }else if( statoTimer.isPausa()){
            updateToolbarAndButtons( R.string.pausa_in_corso);
        }

        //Aggiorna UI al riaccesso all'app dopo che un timer si è concluso
        if( !CountDownTimerService.isRunning && !CountUpTimerService.isRunning && !statoTimer.isPausa()){
            updateTimer(0);
            updateToolbarAndButtons( R.string.pomodoro_disattivo);

        }else if( !PausaTimerService.isRunning && statoTimer.isPausa()){
            switchFragmentFromPausa( statoTimerPrecedente.getValue());
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        //Unregister receivers
        localBroadcastManager.unregisterReceiver( timerBroadcastReceiver);
        localBroadcastManager.unregisterReceiver( toolbarBroadcastReceiver);
        localBroadcastManager.unregisterReceiver( endOfPausaTimerBroadcastReceiver);

        //Unbind from services
        if( CountDownTimerService.isRunning && countDownBounded){
            countDownBounded = false;
            unbindService( countDownServiceConnection);
        }
        if( CountUpTimerService.isRunning && countUpBounded){
            countUpBounded = false;
            unbindService( countUpServiceConnection);
        }
        if( PausaTimerService.isRunning && pausaBounded){
            pausaBounded = false;
            unbindService( pausaServiceConnection);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        saveSharedPreferences();
        startActivity(new Intent(Timer.this, Homepage.class));
    }

    private void setFragmentAndService() {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();

        switch ( statoTimer.getValue()){
            case 0: //COUNTDOWN, ricade nel default
                countDownTimerFragment = new CountDownTimerFragment();
                fragmentTransaction.replace(R.id.placeholder_fragment_timer, countDownTimerFragment);

                if(CountUpTimerService.isRunning) stopService( new Intent(this, CountUpTimerService.class));
                if(PausaTimerService.isRunning) stopService( new Intent( this, PausaTimerService.class));

                //Start and bind of service
                if( !CountDownTimerService.isRunning && !statoTimer.isDisattivo()){

                    if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
                        Log.d(TAG, "Build version "+Build.VERSION.SDK_INT+" vs "+Build.VERSION_CODES.O);
                        startForegroundService( new Intent( this, CountDownTimerService.class));

                    }else{
                        startService( new Intent( this, CountDownTimerService.class));
                    }
                }

                if( !countDownBounded){
                    Intent intent = new Intent( getBaseContext(), CountDownTimerService.class);
                    bindService(intent, countDownServiceConnection, Context.BIND_NOT_FOREGROUND);
                }
                break;
            case 1: //COUNTUP
                countUpTimerFragment = new CountUpTimerFragment();
                fragmentTransaction.replace(R.id.placeholder_fragment_timer, countUpTimerFragment);

                if(PausaTimerService.isRunning) stopService( new Intent( this, PausaTimerService.class));
                if(CountDownTimerService.isRunning) stopService( new Intent(this, CountDownTimerService.class));

                //Start and bind of service
                if( !CountUpTimerService.isRunning && !statoTimer.isDisattivo()){
                    startService( new Intent( this, CountUpTimerService.class));
                }

                if( !countUpBounded){
                    Intent intent = new Intent( getBaseContext(), CountUpTimerService.class);
                    bindService(intent, countUpServiceConnection, Context.BIND_NOT_FOREGROUND);
                }
                break;
            case 2: //PAUSA
                pausaTimerFragment = new PausaTimerFragment();
                fragmentTransaction.replace(R.id.placeholder_fragment_timer, pausaTimerFragment);

                if(CountDownTimerService.isRunning) stopService( new Intent(this, CountDownTimerService.class));
                if(CountUpTimerService.isRunning) stopService( new Intent(this, CountUpTimerService.class));

                if( !PausaTimerService.isRunning && !statoTimer.isDisattivo()){
                    startService( new Intent( this, PausaTimerService.class));
                }

                if( !pausaBounded){
                    Intent intent = new Intent( getBaseContext(), PausaTimerService.class);
                    bindService(intent, pausaServiceConnection, Context.BIND_NOT_FOREGROUND);
                }
                break;

            case 3: //DISATTIVO, ricade nel case default
            default:
                if(CountUpTimerService.isRunning) stopService( new Intent(this, CountUpTimerService.class));
                if(PausaTimerService.isRunning) stopService( new Intent( this, PausaTimerService.class));
                if(CountDownTimerService.isRunning) stopService( new Intent(this, CountDownTimerService.class));

                if( statoTimerPrecedente.isCountDown()){
                    countDownTimerFragment = new CountDownTimerFragment();
                    fragmentTransaction.replace(R.id.placeholder_fragment_timer, countDownTimerFragment);

                }else if( statoTimerPrecedente.isCountUp()){
                    countUpTimerFragment = new CountUpTimerFragment();
                    fragmentTransaction.replace(R.id.placeholder_fragment_timer, countUpTimerFragment);
                }
                break;
        }
        
        fragmentTransaction.commit();
    }

    //Carica i dati dalle shared preferences
    private void loadSharedPreferences() {
        statoTimer.setValue( sharedPreferences.getInt(STATO_TIMER, StatoTimer.DISATTIVO));
        statoTimerPrecedente.setValue( sharedPreferences.getInt(STATO_TIMER_PRECEDENTE, StatoTimer.DISATTIVO));
    }

    private void saveSharedPreferences(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putInt( STATO_TIMER, statoTimer.getValue());
        editor.putInt( STATO_TIMER_PRECEDENTE, statoTimerPrecedente.getValue());
        editor.apply();
    }

    //Implementazione interfaccia del TimerBroadcastReceiver, permette di aggiornare le timer views nei fragments
    @Override
    public void updateTimer(long timeMillis) {

        if(CountDownTimerService.isRunning || CountDownTimerFragment.isActive) {
            countDownTimerFragment.updateTimerView(timeMillis);

        } else if(CountUpTimerService.isRunning || CountUpTimerFragment.isActive) {
            countUpTimerFragment.updateTimerView( timeMillis);

        } else if(PausaTimerService.isRunning) {
            pausaTimerFragment.updateTimerView( timeMillis);
        }
    }

    //Implementazione interfaccia del ToolbarAndButtonsBroadcastReceiver, aggiorna il titolo
    // toolbar e i buttons in base allo stato del timer
    @Override
    public void updateToolbarAndButtons(int stato) {
        toolbar.setTitle( stato);

        if( countDownTimerFragment != null && CountDownTimerFragment.isActive){
            if( CountDownTimerService.isRunning){
                countDownTimerFragment.aggiornaButtons( StatoTimer.COUNTDOWN);

            }else if( stato == R.string.pomodoro_disattivo){
                countDownTimerFragment.aggiornaButtons( StatoTimer.DISATTIVO);
            }
        }

        if( countUpTimerFragment != null && CountUpTimerFragment.isActive){
            if( CountUpTimerService.isRunning){
                countUpTimerFragment.aggiornaButtons( StatoTimer.COUNTUP);

            }else if( stato == R.string.pomodoro_disattivo){
                countUpTimerFragment.aggiornaButtons( StatoTimer.DISATTIVO);
            }
        }

        if( pausaTimerFragment != null && PausaTimerService.isRunning){
            pausaTimerFragment.aggiornaButtons();
        }
    }

    @Override
    public void updateToolbar(int stato){
        updateToolbarAndButtons(stato);
    }

    //Implementazione interfaccia che da un CountDown oppure CountUp porta alla Pausa
    @Override
    public void SwitchToPausa() {
        loadSharedPreferences();
        setFragmentAndService();
        updateToolbarAndButtons( R.string.pausa_in_corso);
    }

    //Implementazione interfaccia che dalla Pausa passa al Countdown
    @Override
    public void switchToCountDown() {
        loadSharedPreferences();
        setFragmentAndService();
        updateToolbarAndButtons( R.string.pomodoro_disattivo);
    }

    //Implementazione interfaccia che dalla Pausa passa al Countup
    @Override
    public void switchToCountup() {
        loadSharedPreferences();
        setFragmentAndService();
        updateToolbarAndButtons( R.string.pomodoro_disattivo);
    }

    //Implementazione interfaccia che alla fine della pausa fa switch di fragment
    @Override
    public void switchFragmentFromPausa(int stato) {
        if( stato == StatoTimer.COUNTDOWN){
            switchToCountDown();

        }else if( stato == StatoTimer.COUNTUP){
            switchToCountup();
        }
    }
}