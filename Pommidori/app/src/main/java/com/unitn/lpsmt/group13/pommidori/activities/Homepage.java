package com.unitn.lpsmt.group13.pommidori.activities;

import static com.unitn.lpsmt.group13.pommidori.Utility.SHARED_PREFS_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.STATO_TIMER;
import static com.unitn.lpsmt.group13.pommidori.Utility.TOOLBAR_BUTTONS_ACTION_INTENT;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.content.ContextCompat;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.StatoTimer;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.fragments.StartNewSessionFragment;
import com.unitn.lpsmt.group13.pommidori.broadcastReceivers.ToolbarAndButtonsBroadcastReceiver;
import com.unitn.lpsmt.group13.pommidori.services.CountDownTimerService;
import com.unitn.lpsmt.group13.pommidori.services.CountUpTimerService;
import com.unitn.lpsmt.group13.pommidori.services.PausaTimerService;

import java.util.Collections;
import java.util.List;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener,
        ToolbarAndButtonsBroadcastReceiver.UpdateToolbarAndButtons{

    private static final String TAG = "Homepage";

    //Variabili
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button calendario;
    private Button newSession;
    private AutoCompleteTextView listaScadenze;
    private AutoCompleteTextView listaSessioni;
    private LocalBroadcastManager localBroadcastManager;
    private ToolbarAndButtonsBroadcastReceiver toolbarBroadcastReceiver;
    private Database db;
    private StatoTimer statoTimer;
    private SharedPreferences sharedPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Inizializzazione variabili
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.homeToolbar);
        listaScadenze = findViewById( R.id.dropdown_scadenze);
        listaSessioni = findViewById( R.id.dropdown_sessioni);
        calendario = findViewById(R.id.hp_calendario);
        newSession = findViewById(R.id.hp_newSession);
        sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        statoTimer = new StatoTimer();

        db = Database.getInstance(this);
        //Al primo avvio dell'app, è necessario inserire l'attività che contiene tutte le sessioni di studio non associate ad attività
        if( !db.exist(this)){
            db.addActivity( new TableActivityModel());
        }

        //Metodi
        setNavigationDrawerMenu();
        setButtonListeners();
        checkPermissions();
    }

    private void checkPermissions() {
        //TODO perché non richiede il permesso notifiche ad avvio app?

        // Register the permissions callback, which handles the user's response to the
        // system permissions dialog. Save the return value, an instance of
        // ActivityResultLauncher, as an instance variable.
        ActivityResultLauncher<String> requestPermissionLauncher = registerForActivityResult(new ActivityResultContracts.RequestPermission(), isGranted -> {
            if (isGranted) {
                // Permission is granted. Continue the action or workflow in your
                // app.
                Log.d(TAG, "permission granted");
            } else {
                // Explain to the user that the feature is unavailable because the
                // feature requires a permission that the user has denied. At the
                // same time, respect the user's decision. Don't link to system
                // settings in an effort to convince the user to change their
                // decision.
                Log.d(TAG, "permission denied");
            }
        });

        //Controlla permission.POST_NOTIFICATIONS, se non granted fa la request
        if ( ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
                == PackageManager.PERMISSION_GRANTED) {
            // You can use the API that requires the permission.
            //performAction(...);
            Log.d(TAG, "POST_NOTIFICATIONS ALREADY GRANTED");
        } else {
            // You can directly ask for the permission.
            // The registered ActivityResultCallback gets the result of this request.
            Log.d(TAG, "POST_NOTIFICATIONS NOT GRANTED");
            requestPermissionLauncher.launch( Manifest.permission.POST_NOTIFICATIONS);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        localBroadcastManager = LocalBroadcastManager.getInstance(this);
        toolbarBroadcastReceiver = new ToolbarAndButtonsBroadcastReceiver(this);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(TOOLBAR_BUTTONS_ACTION_INTENT);
        localBroadcastManager.registerReceiver( toolbarBroadcastReceiver, intentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDropDownLists();
        updateNewSessionButton( checkTimerAttivo());
    }

    //Chiudere il navigation drawer con il pulsante indietro
    @Override
    public void onBackPressed() {
        if(drawerLayout.isDrawerOpen(GravityCompat.START)){
            drawerLayout.closeDrawer(GravityCompat.START);
        }else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

        switch (menuItem.getItemId()){

            case R.id.nav_settings:
                startActivity(new Intent(this, Impostazioni.class));
                break;
            case R.id.nav_report:
                startActivity(new Intent(this, Report.class));
                break;
            default:
                return false;
        }
        return true;
    }

    private void loadSharedPreferences(){
        statoTimer.setValue( sharedPreferences.getInt( STATO_TIMER, StatoTimer.DISATTIVO));
    }

    private void setNavigationDrawerMenu(){
        //settare toolbar com ActionBar dell'Activity
        setSupportActionBar(toolbar);

        //Navigation Drawer Menu
        navigationView.bringToFront();  //viene visualizzato il menuItem quando è selezionato
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar,R.string.navigation_drawer_open,R.string.navigation_drawer_close); //qiando viene premuto il navigationIcon del toolbar, il drawerMenu viene aperto o chiuso
        drawerLayout.addDrawerListener(toggle); //aggiunge il listener al toolbar
        toggle.syncState(); //Sincronizza lo stato tra toolbar e drawer
        navigationView.setNavigationItemSelectedListener(this); //Listener per catturare gli eventi del NavigationView

        //settare titolo e icona del toolbar
        toolbar.setTitle(R.string.app_name);
        toolbar.setNavigationIcon(R.drawable.ic_menu_24);
    }

    private void setButtonListeners(){
        calendario.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Homepage.this,Calendario.class));
            }
        });

        newSession.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if( checkTimerAttivo()){
                    //Timer attivo, vado direttamente all'activity Timer
                    Intent i = new Intent( Homepage.this, Timer.class);
                    startActivity(i);
                }else{
                    //Altrimenti passo per il StartNewSessionFragment
                    StartNewSessionFragment bottomDialogFragment = new StartNewSessionFragment();
                    bottomDialogFragment.show(getSupportFragmentManager(),"MyFragment");
                }
            }
        });
    }

    private void setDropDownLists(){
        db = Database.getInstance( Homepage.this);
        List<TableActivityModel> activity = db.getAllActivitiesFromNow();
        List<TableSessionProgModel> session = db.getFirstProgrammedSessionFromEveryActivityFromNow();

        //L'activity di default non deve essere visualizzata
        TableActivityModel nessunaActivity = new TableActivityModel();
        for(int i=0; i<activity.size(); i++){
            if( activity.get(i).equals(nessunaActivity) ) {
                activity.remove(i);
            }
        }

        Collections.sort( activity);
        Collections.sort( session);

        ArrayAdapter activityAdapter = new ArrayAdapter<TableActivityModel>(
                Homepage.this,
                R.layout.dropdown_item,
                activity
        );
        ArrayAdapter sessionAdapter = new ArrayAdapter<TableSessionProgModel>(
                Homepage.this,
                R.layout.dropdown_item,
                session
        );

        //Se le liste son vuote le lascio tali, altrimenti anteprimo il primo oggetto in lista
        if(!activity.isEmpty()){
            TableActivityModel a = (TableActivityModel) activityAdapter.getItem(0);
            listaScadenze.setText( a.toString());
        }
        if(!session.isEmpty()){
            TableSessionProgModel s = (TableSessionProgModel) sessionAdapter.getItem(0);
            listaSessioni.setText( s.toString());
        }

        listaScadenze.setAdapter( activityAdapter);
        listaSessioni.setAdapter( sessionAdapter);
    }

    //Controlla se è presente un timer in corso
    private boolean checkTimerAttivo(){
        loadSharedPreferences();

        if(CountDownTimerService.isRunning) return true;
        if(CountUpTimerService.isRunning)  return true;
        if(PausaTimerService.isRunning) return true;
        return false;
    }

    //Se timer attivo button newSession "Torna alla sessione" se timer disattivo "Avvia nuova sessione"
    private void updateNewSessionButton( boolean active){
        if( active) newSession.setText( R.string.resume_session);
        else newSession.setText(R.string.new_session);
    }

    @Override
    protected void onStop() {
        super.onStop();
        localBroadcastManager.unregisterReceiver( toolbarBroadcastReceiver);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void updateToolbarAndButtons(int stato) {
        if( stato == R.string.pomodoro_disattivo){
            updateNewSessionButton( false);
        }else{
            updateNewSessionButton( true);
        }
    }
}