package com.unitn.lpsmt.group13.pommidori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.unitn.lpsmt.group13.pommidori.fragments.TimerFragment;

public class Timer extends AppCompatActivity implements TimerFragment.StatoTimerListener {

    //Variabili
    private Toolbar toolbar;
    private Fragment timerFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_timer);

        //Inizializzazione variabili
        toolbar = findViewById(R.id.timerToolbar);
        timerFragment = new TimerFragment();

        //Metodi
        setToolbar();
        setFragment( timerFragment);
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void setToolbar(){
        //settare titolo e icona del toolbar
        toolbar.setTitle(R.string.pomodoro_in_corso);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Timer.this, Homepage.class));
            }
        });
    }

    private void setFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.placeholder_fragment_timer, fragment);
        fragmentTransaction.commit();
    }

    @Override
    public void cambioStatoToolbarTimer(int stato) {
        if(timerFragment != null){
            toolbar.setTitle( stato);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
    }
}