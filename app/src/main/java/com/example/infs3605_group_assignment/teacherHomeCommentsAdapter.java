package com.example.infs3605_group_assignment;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class teacherHomeCommentsAdapter extends RecyclerView.Adapter<teacherHomeCommentsAdapter.MyViewHolder>{


    Context context;
    ArrayList<StudentModel> commentsList;
    public static final String TAG = "t home comments class";
    private OnCommentClickListener mListener;

    public interface OnCommentClickListener{
        void onCommentClick(int position);

    }

    public void setOnCommentClickListener(OnCommentClickListener listener) {
        mListener = listener;
    }

    public teacherHomeCommentsAdapter(Context context, ArrayList<StudentModel> commentsList) {
        this.context = context;
        this.commentsList = commentsList;
    }

    @NonNull
    @Override
    public teacherHomeCommentsAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row_teacher_comments, parent, false);

        return new MyViewHolder(v, mListener);

    }

    @Override
    public void onBindViewHolder(@NonNull teacherHomeCommentsAdapter.MyViewHolder holder, int position) {

        StudentModel sModel = commentsList.get(position);
        holder.uComment.setText(sModel.getLatest_additional_comments());
        holder.uName.setText("-" + sModel.getFirstName() + " " + sModel.getLastName());


        Timestamp t = sModel.getLatest_Timestamp();

        holder.uTime.setText(CalculateTimeSincePosted(t));





    }

    private String CalculateTimeSincePosted(Timestamp t) {

        Date now = new Date();
        Date d = t.toDate();
        long elapsedTime = TimeUnit.MILLISECONDS.toMillis(now.getTime() - d.getTime());

        if (elapsedTime < 60000) {
            //elapsed time between 0 second - 59 sec
            return "Just now";
            //holder.uTime.setText(secTime + " seconds ago");
        } else if (elapsedTime >= 60000 && elapsedTime < 3600000) {
            // Elapsed time between 1 min - 59min
            long minTime = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - d.getTime());
            if (minTime == 1) {
                return String.valueOf(minTime) + " minute ago";
            } else {
                return String.valueOf(minTime) + " minutes ago";
            }

            //holder.uTime.setText(minTime + " mins ago");
        } else {
            long hrTime = TimeUnit.MILLISECONDS.toHours(now.getTime() - d.getTime());
            int time = (int) hrTime;
            if (time >= 24) {

                long dayTime = TimeUnit.MILLISECONDS.toDays(now.getTime() - d.getTime());
                int days = (int) dayTime;
                if (days == 1) {
                    return String.valueOf(days) + " day ago";
                } else {
                    return String.valueOf(days) + " days ago";
                }

            } else if (time == 1) {
                return String.valueOf(hrTime) + " hr ago";
                //holder.uTime.setText(time + " hr ago");
            } else {
                return String.valueOf(hrTime) + " hrs ago";
                //holder.uTime.setText(time + " hrs ago");
            }
        }


    }

    @Override
    public int getItemCount() {


        return commentsList.size();
    }



    public class MyViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView uComment, uName, uTime;
        OnCommentClickListener onCommentClickListener;


        public MyViewHolder(@NonNull View itemView, OnCommentClickListener listener) {
            super(itemView);

            uComment = itemView.findViewById(R.id.tv_user_comment);
            uName = itemView.findViewById(R.id.tv_user_comment_name);
            uTime = itemView.findViewById(R.id.tv_user_comment_time);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if(position != RecyclerView.NO_POSITION) {
                            listener.onCommentClick(position);
                        }
                    }
                }
            });

        }

        @Override
        public void onClick(View view) {

        }
    }







}
