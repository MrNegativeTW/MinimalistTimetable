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

    String[] Items={"08:00 ~ 09:50","10:10 ~ 12:00","13:30 ~ 15:20"};
    String[] CourseName={"慢速壘球","科技新知通論","台灣生態環境與資源保護"};
    //String[] CourseName = getResources().getStringArray(R.array.courseName_Mon);
    String[] CoursePlace={"林森 - 操場", "林森 - 敬業樓509", "屏商 - 二館中M314"};

    public Frag1(){
        // Required empty public constructor
    }


    public void onStart() {
        super.onStart();
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
