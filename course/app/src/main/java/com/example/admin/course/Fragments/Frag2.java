package com.example.admin.course.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.course.Adapter;
import com.example.admin.course.R;

public class Frag2 extends Fragment {
    private RecyclerView recyclerView;

    public Frag2(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();

        String[] Items = getActivity().getResources().getStringArray(R.array.courseTime_Tue);
        String[] CourseName = getActivity().getResources().getStringArray(R.array.courseName_Tue);
        String[] CoursePlace = getActivity().getResources().getStringArray(R.array.coursePlace_Tue);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview2);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace, 1));
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        View frag2 = inflater.inflate(R.layout.frag2_layout, container, false);
        return frag2;
    }
}
