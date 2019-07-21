package com.example.admin.course.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.course.Adapter;
import com.example.admin.course.DBHandler;
import com.example.admin.course.R;

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
