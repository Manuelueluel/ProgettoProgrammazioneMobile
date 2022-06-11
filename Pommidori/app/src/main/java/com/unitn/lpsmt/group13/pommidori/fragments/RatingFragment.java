package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Rating;
import com.unitn.lpsmt.group13.pommidori.utils.RatingAdapter;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String counter;
	private String mParam2;
	private String mParam1;
	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private ArrayList<Rating> list;
	private String names[];

	public RatingFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param counter Parameter 1.
	 * @return A new instance of fragment RatingFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static RatingFragment newInstance(int counter) {
		RatingFragment fragment = new RatingFragment();
		Bundle args = new Bundle();
		args.putInt(ARG_PARAM1, counter);
		//args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			counter = getArguments().getString(ARG_PARAM1);
			//mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_rating, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		names = getResources().getStringArray(R.array.activitys_name);
		list = generaListaRatings();

		recyclerView = view.findViewById(R.id.recyclerview_ratings);
		layoutManager = new LinearLayoutManager( view.getContext());
		adapter = new RatingAdapter( list);
		recyclerView.setLayoutManager( layoutManager);
		recyclerView.setAdapter( adapter);
	}

	private ArrayList<Rating> generaListaRatings(){
		ArrayList<Rating> returnList = new ArrayList<>();
		int size = ThreadLocalRandom.current().nextInt(2, names.length);

		for(int i=0; i<size; i++){
			returnList.add( new Rating( getRandomRating(), getRandomActivityName( names)));
		}
		return returnList;
	}

	private String getRandomActivityName(String names[]){
		return names[ThreadLocalRandom.current().nextInt(0, names.length)];
	}

	private float getRandomRating(){
		float f = (ThreadLocalRandom.current().nextFloat() * 10) % 5;
		return 	f;
	}
}