package com.example.admin.course;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    String[] items; //courseTime
    String[] courseName;
    String[] coursePlace;
    int fragment;

    public Adapter(Context context, String[] items, String[] courseName, String[] coursePlace, int fragment){
        this.context = context;
        this.items = items;
        this.courseName = courseName;
        this.coursePlace = coursePlace;
        this.fragment = fragment;
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View row = inflater.inflate(R.layout.costume_row, parent, false);
        Item item = new Item(row);

        /**
         * if return null, will cause ~'RecyclerView.ViewHolder' on a null object reference,
         * instead using item, cause Normal Running.
         */
        return item;
    }

    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        ((Item)holder).textView.setText(items[position]);
        ((Item)holder).textView2.setText(courseName[position]);
        ((Item)holder).textView3.setText(coursePlace[position]);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String title = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.title)).getText().toString();
                TextView text = (TextView) view.findViewById(R.id.item);
                switch (fragment) {
                    case 0:
                        Log.i("Test", "禮拜1 的 " + position + text.getText().toString());
                        break;
                    case 1:
                        Log.i("Test", "禮拜2 的 " + position);
                        break;
                    case 2:
                        Log.i("Test", "禮拜3 的 " + position);
                        break;
                    case 3:
                        Log.i("Test", "禮拜4 的 " + position);
                        break;
                    case 4:
                        Log.i("Test", "禮拜5 的 " + position);
                        break;
                }
            } //.onClick


        });
    }

    @Override
    public int getItemCount() {
        return items.length;
    }


    public class Item extends RecyclerView.ViewHolder /*implements View.OnClickListener*/ {

        TextView textView;
        TextView textView2;
        TextView textView3;

        public Item(View itemView) {
            super(itemView);
            textView = (TextView) itemView.findViewById(R.id.item);
            textView2 = (TextView) itemView.findViewById(R.id.item2);
            textView3 = (TextView) itemView.findViewById(R.id.item3);
//            itemView.setOnClickListener(this);
        }

//        @Override
//        public void onClick(View view) {
//            switch (fragment) {
//                case 0:
//                    Log.i("Test", "禮拜一");
//                    break;
//                case 1:
//                    Log.i("Test", "禮拜二");
//                    break;
//                case 2:
//                    Log.i("Test", "禮拜3");
//                    break;
//                case 3:
//                    Log.i("Test", "禮拜4");
//                    break;
//                case 4:
//                    Log.i("Test", "禮拜5");
//                    break;
//            }
//        }

    } // .class

}
