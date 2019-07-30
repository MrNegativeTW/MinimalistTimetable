package com.txwstudio.app.timetable;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class Adapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private Activity mContext;
//    private Context mContext;
    private ArrayList<Course> courseArrayList;
    DBHandler db;
    private int fragment;

    public Adapter(Activity mContext, ArrayList<Course> arrayList, int fragment){
        this.mContext = mContext;
        this.courseArrayList = arrayList;
        this.fragment = fragment;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
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

        String startTime = courseArrayList.get(position).getCourseStartTime();
        String endTime = courseArrayList.get(position).getCourseEndTime();
        String startTimeFormat = startTime.replaceAll("..(?!$)", "$0:");
        String endTimeFormat = endTime.replaceAll("..(?!$)", "$0:");
        String timeToShow = startTimeFormat + " ~ " + endTimeFormat;

        ((Item)holder).textView.setText(timeToShow);
        ((Item)holder).textView2.setText(Name);
        ((Item)holder).textView3.setText(Place);


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView text = (TextView) view.findViewById(R.id.item2);
                int dateDebug = fragment + 1;
                Toast.makeText(mContext, "禮拜" + dateDebug +"的"+ text.getText().toString(),
                        Toast.LENGTH_SHORT).show();
            }
        });


        // Long Click will show up an dialog window to ask user want to edit or delete.
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View view) {

                AlertDialog.Builder dialog = new AlertDialog.Builder(mContext);
                dialog.setItems(R.array.dialog, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db = new DBHandler(mContext);
                        int ID = courseArrayList.get(position).getID();
                        switch (i) {
                            case 0:
                                Intent intent = new Intent(mContext, CourseEditActivity.class);
                                intent.putExtra("ID", ID);
                                mContext.startActivity(intent);

                                courseArrayList.clear();
                                courseArrayList = db.getCourse(fragment);
                                notifyDataSetChanged();
                                break;
                            case 1:
                                db.deleteCourse(ID);
                                courseArrayList.remove(position);
                                notifyDataSetChanged();
                                Toast.makeText(mContext, R.string.dialogDeleted, Toast.LENGTH_SHORT).show();
                                break;
                        }
                    }
                });
                dialog.show();

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
        }
    }

}