package com.unitn.lpsmt.group13.pommidori.fragments;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.navigation.fragment.NavHostFragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.unitn.lpsmt.group13.pommidori.R;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link TortaSettimanaFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class TortaSettimanaFragment extends Fragment {

	// TODO: Rename parameter arguments, choose names that match
	// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
	private static final String ARG_PARAM1 = "param1";
	private static final String ARG_PARAM2 = "param2";

	// TODO: Rename and change types of parameters
	private String mParam1;
	private String mParam2;

	public TortaSettimanaFragment() {
		// Required empty public constructor
	}

	/**
	 * Use this factory method to create a new instance of
	 * this fragment using the provided parameters.
	 *
	 * @param param1 Parameter 1.
	 * @param param2 Parameter 2.
	 * @return A new instance of fragment TortaSettimanaFragment.
	 */
	// TODO: Rename and change types and number of parameters
	public static TortaSettimanaFragment newInstance(String param1, String param2) {
		TortaSettimanaFragment fragment = new TortaSettimanaFragment();
		Bundle args = new Bundle();
		args.putString(ARG_PARAM1, param1);
		args.putString(ARG_PARAM2, param2);
		fragment.setArguments(args);
		return fragment;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (getArguments() != null) {
			mParam1 = getArguments().getString(ARG_PARAM1);
			mParam2 = getArguments().getString(ARG_PARAM2);
		}
	}

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
							 Bundle savedInstanceState) {
		// Inflate the layout for this fragment
		return inflater.inflate(R.layout.fragment_torta_settimana, container, false);
	}

	@Override
	public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
		super.onViewCreated(view, savedInstanceState);

		Button toTortaMese = view.findViewById(R.id.btn_fromTortaSettimana_toTortaMese);
		Button toProgrammazione = view.findViewById(R.id.btn_fromTortaSettimana_toProgrammazione);
		Button toValutazione = view.findViewById(R.id.btn_fromTortaSettimana_toValutazione);

		toTortaMese.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavHostFragment.findNavController( TortaSettimanaFragment.this)
						.navigate(R.id.action_tortaSettimanaFragment_to_tortaMeseFragment);
			}
		});

		toValutazione.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavHostFragment.findNavController( TortaSettimanaFragment.this)
						.navigate(R.id.action_tortaSettimanaFragment_to_valutazioneFragment);
			}
		});

		toProgrammazione.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				NavHostFragment.findNavController( TortaSettimanaFragment.this)
						.navigate(R.id.action_tortaSettimanaFragment_to_programmazioneFragment);
			}
		});
	}
}