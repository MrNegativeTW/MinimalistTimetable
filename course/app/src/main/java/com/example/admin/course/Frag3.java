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

public class Frag3 extends Fragment {

    private RecyclerView recyclerView;

    String[] Items={"09:00 ~ 12:00","13:30 ~ 15:20"};
    String[] CourseName={"演算法", "進階英文B"};
    String[] CoursePlace={"林森 - 五育樓3F視聽", "林森 - 五育樓109"};

    public Frag3(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();
        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview3);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace));
    }

    @Override
    public View onCreateView( LayoutInflater inflater,  ViewGroup container,  Bundle savedInstanceState) {
        return inflater.inflate(R.layout.frag3_layout, container, false);
    }
}
