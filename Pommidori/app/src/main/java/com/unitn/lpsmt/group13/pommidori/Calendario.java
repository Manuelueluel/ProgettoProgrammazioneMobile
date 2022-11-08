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
import com.unitn.lpsmt.group13.pommidori.fragments.ModifySessionFragment;

import java.util.Date;

public class Calendario extends AppCompatActivity implements ModifySessionFragment.UpdateCalendarEventListListener {

    private Toolbar toolbar;
    private FloatingActionButton floatingActionButton;
    private CustomCalendarMonthFragment calendarMonthFragment;

    @Override
    public void updateEventList() {
        calendarMonthFragment.clearCalendar();
        calendarMonthFragment.showEventsToCalendar();
        calendarMonthFragment.loadCalendarEventList( new Date());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_calendario);

        toolbar = findViewById(R.id.calendarToolbar);
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
        toolbar.setTitle(R.string.calendar);
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
        calendarMonthFragment = new CustomCalendarMonthFragment();
        fragmentTransaction.replace(R.id.calendar_fragment, calendarMonthFragment);
        fragmentTransaction.commit();
    }
}