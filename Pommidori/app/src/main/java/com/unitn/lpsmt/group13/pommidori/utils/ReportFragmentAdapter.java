package com.unitn.lpsmt.group13.pommidori.utils;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.unitn.lpsmt.group13.pommidori.fragments.PieChartFragment;
import com.unitn.lpsmt.group13.pommidori.fragments.ProgressFragment;
import com.unitn.lpsmt.group13.pommidori.fragments.RatingFragment;

public class ReportFragmentAdapter extends FragmentStateAdapter {

	public static final int FRAGMENTS_NUMBER = 3;

	public ReportFragmentAdapter(@NonNull FragmentActivity fragmentActivity) {
		super(fragmentActivity);
	}

	@NonNull
	@Override
	public Fragment createFragment(int position) {
		Fragment fragment;

		switch (position){
			case 0: fragment = PieChartFragment.newInstance(1);
				break;
			case 1: fragment = ProgressFragment.newInstance(2);
				break;
			case 2: fragment = RatingFragment.newInstance(3);
				break;
			default:
				fragment = new Fragment();
				break;
		}
		return fragment;
	}

	@Override
	public int getItemCount() {
		return FRAGMENTS_NUMBER;
	}
}
