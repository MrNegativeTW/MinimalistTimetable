package com.txwstudio.app.timetable;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    Context context;
    private ArrayList<Course> courseArrayList;
    DBHandler db;
    private int fragment;

    public Adapter(Context context, ArrayList<Course> arrayList, int fragment){
        this.context = context;
        this.courseArrayList = arrayList;
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

        // Get each value in ArrayList, then set them to text.
        String Name = courseArrayList.get(position).getCourseName();
        String Place = courseArrayList.get(position).getCoursePlace();
        String StartTime = courseArrayList.get(position).getCourseStartTime();
        ((Item)holder).textView.setText(StartTime);
        ((Item)holder).textView2.setText(Name);
        ((Item)holder).textView3.setText(Place);

        // Single Click: Edit item
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                String title = ((TextView) recyclerView.findViewHolderForAdapterPosition(position).itemView.findViewById(R.id.title)).getText().toString();
                TextView text = (TextView) view.findViewById(R.id.item);
                int dateDebug = fragment + 1;
                Log.i("Test", "禮拜 " + dateDebug + "的第" + position + "個，時間" + text.getText().toString());
                notifyDataSetChanged();
//                switch (fragment) {
//                    case 0:
//                        Log.i("Test", "禮拜1 的第 " + position + "個，時間" + text.getText().toString());
//                        break;
//                    case 1:
//                        Log.i("Test", "禮拜2 的第 " + position + "個");
//                        break;
//                    case 2:
//                        Log.i("Test", "禮拜3 的第 " + position + "個");
//                        break;
//                    case 3:
//                        Log.i("Test", "禮拜4 的第 " + position + "個");
//                        break;
//                    case 4:
//                        Log.i("Test", "禮拜5 的第 " + position + "個");
//                        break;
//                }
            } //.onClick
        }); // .setOnClickListener

        // Long Click: Delete item
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                db = new DBHandler(context);

                String ID = courseArrayList.get(position).getID();

                db.deleteCourse(ID);
                db.getCourse(fragment);
                courseArrayList.remove(position);
                notifyDataSetChanged();
                Toast.makeText(context, "已刪除", Toast.LENGTH_SHORT).show();

                return true;
            } // .onLongClick
        }); // .setOnLongClickListener
    }


    @Override
    public int getItemCount() {
        return courseArrayList.size();
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
