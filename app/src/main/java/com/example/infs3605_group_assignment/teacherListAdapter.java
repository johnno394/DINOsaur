package com.example.infs3605_group_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class teacherListAdapter extends RecyclerView.Adapter<teacherListAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model_Student> listArray;
    public static final String TAG = "studentListAdapter";

    private studentListAdapter.onItemClickListener mListener;


    public teacherListAdapter(Context context, ArrayList<Model_Student> listArray) {
        this.context = context;
        this.listArray = listArray;
    }

    @NonNull
    @Override
    public teacherListAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(context).inflate(R.layout.recyclerview_grid_item, parent, false);

        return new teacherListAdapter.MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull teacherListAdapter.MyViewHolder holder, int position) {
        Model_Student sModel = listArray.get(position);
        holder.studentName.setText(sModel.getFirstName() + " " + sModel.getLastName());
        holder.studentImage.setImageDrawable(context.getResources().getDrawable(R.drawable.profile_pic));

    }

    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView studentName;
        ImageView studentImage;

        public MyViewHolder(@NonNull View itemView, studentListAdapter.onItemClickListener listener) {
            super(itemView);

            studentName = itemView.findViewById(R.id.studentName);
            studentImage = itemView.findViewById(R.id.studentImage);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null){
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION){
                            listener.onItemClick(position);
                        }
                    }
                }
            });
        }
    }

    public interface onItemClickListener{
        void onItemClick(int position);
    }

    public void setOnItemClickListener(studentListAdapter.onItemClickListener listener){
        mListener = listener;
    }


}
