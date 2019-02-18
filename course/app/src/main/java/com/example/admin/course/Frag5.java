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

public class Frag5 extends Fragment {

    private RecyclerView recyclerView;

        public Frag5(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();

        String[] Items = getActivity().getResources().getStringArray(R.array.courseTime_Fri);
        String[] CourseName = getActivity().getResources().getStringArray(R.array.courseName_Fri);
        String[] CoursePlace = getActivity().getResources().getStringArray(R.array.coursePlace_Fri);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview5);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace));
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag5_layout, container, false);
    }
}
