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

public class Frag4 extends Fragment {

    private RecyclerView recyclerView;

    String[] Items={"09:00 ~ 12:00","13:30 ~ 16:30"};
    String[] CourseName={"ios 程式設計","Java 程式設計"};
    String[] CoursePlace={"林森 - 教科館遠距暨微型", "林森 - 五育樓電B"};

    public Frag4(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview4);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace));
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag4_layout, container, false);
    }
}
