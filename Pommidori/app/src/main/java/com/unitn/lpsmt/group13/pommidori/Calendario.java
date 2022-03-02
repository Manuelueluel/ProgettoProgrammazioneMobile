package com.unitn.lpsmt.group13.pommidori;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.unitn.lpsmt.group13.pommidori.fragments.CustomCalendarMonthFragment;

public class Calendario extends AppCompatActivity {

    private Toolbar toolbar;
    //OldCustomCalendarView oldCustomCalendarView;
    private FloatingActionButton floatingActionButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        toolbar = findViewById(R.id.calendarToolbar);
        //oldCustomCalendarView = (OldCustomCalendarView) findViewById(R.id.custom_calendar_view);
        floatingActionButton = findViewById(R.id.floatingActionButton);

        //Metodi
        setToolbar();
        setButtonListeners();
        setFragment();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void setToolbar(){
        //settare titolo e icona del toolbar
        toolbar.setTitle("Calendario");
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Calendario.this, Homepage.class));
            }
        });
    }

    private void setButtonListeners(){
        floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Calendario.this, NewActivityAndSession.class));
            }
        });
    }

    public void setFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.calendar_fragment, new CustomCalendarMonthFragment());
        fragmentTransaction.commit();
    }
}