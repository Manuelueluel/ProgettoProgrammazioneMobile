package com.unitn.lpsmt.group13.pommidori;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import com.google.android.material.navigation.NavigationView;
import com.unitn.lpsmt.group13.pommidori.db.TableActivityModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.fragments.StartNewSessionFragment;

import java.util.Collections;
import java.util.List;

public class Homepage extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    //Variabili
    private DrawerLayout drawerLayout;
    private NavigationView navigationView;
    private Toolbar toolbar;
    private Button calendario;
    private Button newSession;
    private Button btnScadenze;
    private Button btnSessioni;
    private AutoCompleteTextView listaScadenze;
    private AutoCompleteTextView listaSessioni;
    private InterceptEventLayout interceptEventScadenze;
    private InterceptEventLayout interceptEventSessioni;

    private Database db;
    private StatoTimer statoTimer;

    //Shared Preferances
    private final String SHARED_PREFS_TIMER = Utility.SHARED_PREFS_TIMER;
    private final String STATO_TIMER = Utility.STATO_TIMER;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_homepage);

        //Inizializzazione variabili
        drawerLayout = findViewById(R.id.drawer_layout);
        navigationView = findViewById(R.id.nav_view);
        toolbar = findViewById(R.id.homeToolbar);

        btnScadenze = (Button) findViewById( R.id.btn_dropdown_scadenze);
        btnSessioni = (Button) findViewById( R.id.btn_dropdown_sessioni);

        listaScadenze = (AutoCompleteTextView) findViewById( R.id.dropdown_scadenze);
        listaSessioni = (AutoCompleteTextView) findViewById( R.id.dropdown_sessioni);

        interceptEventScadenze = (InterceptEventLayout) findViewById( R.id.wrap_prossime_scadenze);
        interceptEventSessioni = (InterceptEventLayout) findViewById( R.id.wrap_prossime_sessioni);

        calendario = findViewById(R.id.hp_calendario);
        newSession = findViewById(R.id.hp_newSession);

        db = Database.getInstance(this);
        //Al primo avvio dell'app, è necessario inserire l'attività che contiene tutte le sessioni di studio non associate ad attività
        if( !db.exist(this)){
            db.addActivity( new TableActivityModel());
        }

        //Metodi
        setNavigationDrawerMenu();
        setButtonListeners();
    }

    @Override
    protected void onResume() {
        super.onResume();
        setDropDownLists();
        if( checkTimerAttivo()) newSession.setText( R.string.resume_session);
        else newSession.setText(R.string.new_session);
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
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS_TIMER, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        //Se non è presente un timer attivo, viene comunque salvato lo stato come disattivo
        int stato = sharedPreferences.getInt(STATO_TIMER, StatoTimer.DISATTIVO);
        statoTimer = new StatoTimer( stato);

        editor.putInt(STATO_TIMER, statoTimer.getValue());
        editor.apply();

        if( statoTimer.isDisattivo()) return false;
        return true;
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d("Homepage", "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("Homepage", "onDestroy");
    }
}