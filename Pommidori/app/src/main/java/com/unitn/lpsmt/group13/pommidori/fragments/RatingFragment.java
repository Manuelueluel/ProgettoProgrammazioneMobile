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

import com.unitn.lpsmt.group13.pommidori.db.Database;
import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.Rating;
import com.unitn.lpsmt.group13.pommidori.db.TablePomodoroModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionModel;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;
import com.unitn.lpsmt.group13.pommidori.utils.RatingAdapter;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RatingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RatingFragment extends Fragment {

	private RecyclerView recyclerView;
	private RecyclerView.Adapter adapter;
	private RecyclerView.LayoutManager layoutManager;
	private Database database;

	public RatingFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment.
	 *
	 * @return A new instance of fragment RatingFragment.
	 */
	public static RatingFragment newInstance() {
		return new RatingFragment();
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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

		recyclerView = view.findViewById(R.id.recyclerview_ratings);
		layoutManager = new LinearLayoutManager( view.getContext());
		recyclerView.setLayoutManager( layoutManager);
	}

	@Override
	public void onResume() {
		super.onResume();
		database = Database.getInstance( this.getContext());
		getListOfRatings();
	}

	//TODO getListOfRatings da testare
	private void getListOfRatings(){
		List<TableSessionProgModel> progSessionsList = database.getAllPastProgrammedSessions();
		List<TablePomodoroModel> pomodoroList = database.getAllPastPomodoro();

		List<TableSessionModel> sessionsList = database.getAllSessions();
		HashSet<Rating> ratings = new HashSet<>();







		sessionsList.forEach( sessionModel -> {
			Rating r = new Rating( sessionModel.getValutazione(), sessionModel.getName());
			if( ratings.contains(r)){
				ratings.add( new Rating( sessionModel.getValutazione()+r.getRating(), r.getActivityName()));
			}else{
				ratings.add(r);
			}
		});



		ArrayList<Rating> list = new ArrayList<>( ratings);
		adapter = new RatingAdapter( list);
		recyclerView.setAdapter( adapter);
	}

	/*
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

	 */
}