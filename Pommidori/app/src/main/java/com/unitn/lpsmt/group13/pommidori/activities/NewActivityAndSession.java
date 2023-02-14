package com.unitn.lpsmt.group13.pommidori.activities;

import android.os.Bundle;

import com.google.android.material.tabs.TabLayout;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.utils.FragmentAdapter;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager2.widget.ViewPager2;

public class NewActivityAndSession extends AppCompatActivity {

    private ViewPager2 viewPager2;
    private TabLayout tabLayout;
    private FragmentAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.new_activity_and_session);

        viewPager2 = findViewById(R.id.fragment_form_new);
        tabLayout = findViewById(R.id.tab_layout);

        setUpViewPager(viewPager2);
    }

    private void setUpViewPager(ViewPager2 pager2) {
        FragmentManager fm = getSupportFragmentManager();
        adapter = new FragmentAdapter(fm, getLifecycle());

        pager2.setAdapter(adapter);

        tabLayout.addTab(tabLayout.newTab().setText( getString(R.string.new_activity)));
        tabLayout.addTab(tabLayout.newTab().setText(R.string.new_session));

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                pager2.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        pager2.registerOnPageChangeCallback(new ViewPager2.OnPageChangeCallback() {
            @Override
            public void onPageSelected(int position) {
                tabLayout.selectTab(tabLayout.getTabAt(position));
            }
        });
    }
}
