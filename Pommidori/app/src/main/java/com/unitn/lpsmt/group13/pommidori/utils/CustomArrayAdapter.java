package com.unitn.lpsmt.group13.pommidori.utils;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.unitn.lpsmt.group13.pommidori.R;
import com.unitn.lpsmt.group13.pommidori.db.TableSessionProgModel;

import java.util.ArrayList;


public class CustomArrayAdapter extends ArrayAdapter {

    private ArrayList<TableSessionProgModel> tSPMArray = null;

    public CustomArrayAdapter( Context context, int res, ArrayList<TableSessionProgModel> session) {
        super(context,res);
        this.tSPMArray = session;
    }
    @Override
    public int getCount()
    {
        return tSPMArray.size();
    }
    @Override
    public Object getItem(int position)
    {
        return tSPMArray.get(position);
    }
    @Override
    public View getView(int position, View v, ViewGroup vg){
        if(v==null)
            v= LayoutInflater.from(getContext()).inflate(R.layout.row,null);

        TableSessionProgModel tSPM = (TableSessionProgModel) getItem(position);

        TextView text = v.findViewById(R.id.row_text);
        text.setText( tSPM.getActivity().getName());

        v.setBackgroundColor(tSPM.getActivity().getColore());

        return v;
    }
}
