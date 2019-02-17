package com.example.admin.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    Context context;
    String[] items; //courseTime
    String[] courseName;
    String[] coursePlace;

    public Adapter(Context context, String[] items, String[] courseName, String[] coursePlace){
        this.context = context;
        this.items = items;
        this.courseName = courseName;
        this.coursePlace = coursePlace;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.costume_row, parent, false);
        Item item = new Item(row);
        //Item CourseTime = new Item(row);

        /**
         * if return null, will cause ~'RecyclerView.ViewHolder' on a null object reference, instead using item, cause Normal Running.
         */
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ((Item)holder).textView.setText(items[position]);
        ((Item)holder).textView2.setText(courseName[position]);
        ((Item)holder).textView3.setText(coursePlace[position]);
        //((CourseTime)holder).textView.setText(courseTime[position]);

    }

    @Override
    public int getItemCount() {
        return items.length;
    }


    public class Item extends RecyclerView.ViewHolder {
        TextView textView;
        TextView textView2;
        TextView textView3;
        public Item(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
            textView2 = (TextView) itemView.findViewById(R.id.item2);
            textView3 = (TextView) itemView.findViewById(R.id.item3);
        }
    }
/*
    public class CourseTime extends RecyclerView.ViewHolder {
        TextView textView;
        public CourseTime(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item2);
        }
    }
*/
}
