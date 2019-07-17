package com.example.admin.course.Fragments;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.admin.course.Adapter;
import com.example.admin.course.DBHandler;
import com.example.admin.course.R;

import java.util.ArrayList;
import java.util.List;


public class Frag1 extends Fragment {

    private RecyclerView recyclerView;
    DBHandler mDBHandler;


    public Frag1(){
        // Required empty public constructor
    }

    public void onStart() {
        super.onStart();

//        String[] Items = getActivity().getResources().getStringArray(R.array.courseTime_Mon);
//        String[] CourseName = getActivity().getResources().getStringArray(R.array.courseName_Mon);
//        String[] CoursePlace = getActivity().getResources().getStringArray(R.array.coursePlace_Mon);

        recyclerView = (RecyclerView) getActivity().findViewById(R.id.recyclerview1);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(new Adapter(getActivity(), Items, CourseName, CoursePlace, 0));
    }

    private List<String> ItemsList = new ArrayList<String>();
    private List<String> CourseNameList = new ArrayList<String>();
    private List<String> CoursePlaceList = new ArrayList<String>();
    private String[] Items;
    private String[] CourseName;
    private String[] CoursePlace;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mDBHandler = new DBHandler(getActivity());
        Cursor data = mDBHandler.getCourse(4);

        while (data.moveToNext()) {
            ItemsList.add(data.getString(4));
            CourseNameList.add(data.getString(1));
            CoursePlaceList.add(data.getString(2));
        }

        Items = ItemsList.toArray(new String[0]);
        CourseName = CourseNameList.toArray(new String[0]);
        CoursePlace = CoursePlaceList.toArray(new String[0]);
        Log.i("Test", String.valueOf(CourseNameList));
    }

    @Override
    public View onCreateView( LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View frags1 = inflater.inflate(R.layout.frag1_layout, container, false);
        return frags1;
    }
}
