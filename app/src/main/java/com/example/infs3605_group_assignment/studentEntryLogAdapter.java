package com.example.infs3605_group_assignment;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class studentEntryLogAdapter extends RecyclerView.Adapter<studentEntryLogAdapter.MyViewHolder> {

    Context context;
    ArrayList<WellbeingModel> logsArrayList;
    public static final String TAG = "student Adapter class";

    public studentEntryLogAdapter(Context context, ArrayList<WellbeingModel> logsArrayList) {
        this.context = context;
        this.logsArrayList = logsArrayList;
    }

    @NonNull
    @Override
    public studentEntryLogAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row, parent, false);



        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull studentEntryLogAdapter.MyViewHolder holder, int position) {

        WellbeingModel wModel = logsArrayList.get(position);

        //set emoji
        int emoRating = wModel.getEmotional_rating().intValue();
        switch(emoRating){
            case 1:
                holder.emojiState.setText("Horrible");
                holder.emoji.setImageDrawable(context.getDrawable(R.drawable.emoji_1));
                break;

            case 2:
                holder.emojiState.setText("Not Great");
                holder.emoji.setImageDrawable(context.getDrawable(R.drawable.emoji_2));
                break;

            case 3:
                holder.emojiState.setText("Ok");
                holder.emoji.setImageDrawable(context.getDrawable(R.drawable.emoji_3));
                break;

            case 4:
                holder.emojiState.setText("Pleasant");
                holder.emoji.setImageDrawable(context.getDrawable(R.drawable.emoji_4));
                break;

            case 5:
                holder.emojiState.setText("Great");
                holder.emoji.setImageDrawable(context.getDrawable(R.drawable.emoji_5));
                break;

        }

        /*
        //set emoji
        String nameOfPicture = "emoji_" + String.valueOf(emoScore);
        int resId = getResources().getIdentifier(nameOfPicture, "drawable", getActivity().getPackageName());
        Drawable image = getResources().getDrawable(resId);
        emojiFace.setImageDrawable(image);

         */

        // Set time attributes
        Timestamp t = wModel.getTimestamp();

        SimpleDateFormat fDay = new SimpleDateFormat("EEEE");

        SimpleDateFormat fDate = new SimpleDateFormat("MMM dd");

        SimpleDateFormat fTime = new SimpleDateFormat("hh:mm a");

        Date d = t.toDate();
        String sfDay = fDay.format(d);
        holder.timeDay.setText(sfDay);

        String sfDate = fDate.format(d);
        holder.timeDate.setText(sfDate);

        String sfTime = fTime.format(d);
        holder.timeTime.setText(sfTime);

        // Set intensity
        holder.emoInt.setText(wModel.getIntensity_of_emotion().toString());

        // Set Comments
        holder.comments.setText(wModel.getAdditional_comments().toString());



    }

    @Override
    public int getItemCount() {
        return logsArrayList.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        ImageView emoji;
        TextView emojiState;

        TextView timeDay, timeDate, timeTime, emoInt, comments;

        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            emoji = itemView.findViewById(R.id.logs_iv_emoji);
            emojiState = itemView.findViewById(R.id.logs_tv_emo_state);

            timeDay = itemView.findViewById(R.id.logs_tv_timestamp_day);
            timeDate = itemView.findViewById(R.id.logs_tv_timestamp_date);
            timeTime = itemView.findViewById(R.id.logs_tv_timestamp_time);

            emoInt = itemView.findViewById(R.id.logs_tv_emoIntensity);
            comments = itemView.findViewById(R.id.logs_tv_Comments);


        }
    }
}














