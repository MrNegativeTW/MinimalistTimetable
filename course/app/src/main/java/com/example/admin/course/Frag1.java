package com.example.admin.course;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


public class Frag1 extends Fragment {

    private RecyclerView recyclerView;

    public Frag1(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();

        String[] Items = getActivity().getResources().getStringArray(R.array.courseTime_Mon);
        String[] CourseName = getActivity().getResources().getStringArray(R.array.courseName_Mon);
        String[] CoursePlace = getActivity().getResources().getStringArray(R.array.coursePlace_Mon);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace));
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View frags1 = inflater.inflate(R.layout.frag1_layout, container, false);
        return frags1;


    }
}
