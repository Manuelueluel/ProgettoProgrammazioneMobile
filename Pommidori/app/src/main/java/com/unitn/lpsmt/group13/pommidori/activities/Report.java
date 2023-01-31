package com.unitn.lpsmt.group13.pommidori.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.utils.ReportFragmentAdapter;

public class Report extends AppCompatActivity {

    private Toolbar toolbar;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);

        toolbar = findViewById(R.id.reportToolbar);
        tabLayout = findViewById(R.id.tabs);
        viewPager = findViewById(R.id.view_pager);


        setToolbar();
        setTabs();
    }

    @Override
    public void onBackPressed() {
        this.finish();
    }

    private void setToolbar(){
        toolbar.setTitle(R.string.report);
        toolbar.setNavigationIcon(R.drawable.ic_arrow_back_24);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(Report.this, Homepage.class));
            }
        });
    }

    private void setTabs(){
        viewPager.setUserInputEnabled( false);
        viewPager.setAdapter( new ReportFragmentAdapter( this));

        new TabLayoutMediator(tabLayout, viewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("Chart");
                                break;
                            case 1:
                                tab.setText("Progress");
                                break;
                            case 2:
                                tab.setText("Rating");
                                break;
                            default:
                                break;
                        }
                    }
                }).attach();
    }

}