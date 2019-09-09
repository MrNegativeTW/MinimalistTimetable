package com.txwstudio.app.timetable.Fragments;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.txwstudio.app.timetable.Adapter;
import com.txwstudio.app.timetable.DBHandler;
import com.txwstudio.app.timetable.R;

public class Frag1 extends Fragment {

    private RecyclerView recyclerView;
    private DBHandler db;

    public Frag1(){}

    public void onStart() {
        super.onStart();

        db = new DBHandler(getActivity());

        // Setup Adapter
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), db.getCourse(0), 0));
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag1_layout, container, false);
    }
}
