package com.example.infs3605_group_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class studentListAdapter extends RecyclerView.Adapter<studentListAdapter.MyViewHolder> {

    Context context;
    ArrayList<StudentModel> listArray;
    public static final String TAG = "studentListAdapter";

    private onItemClickListener mListener;

    public studentListAdapter(Context context, ArrayList<StudentModel> listArray) {
        this.context = context;
        this.listArray = listArray;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row_teacher, parent, false);

        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        StudentModel sModel = listArray.get(position);
        holder.tvFullName.setText(sModel.getFirstName() + " " + sModel.getLastName());


    }


    @Override
    public int getItemCount() {
        return listArray.size();
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {

        TextView tvFullName;

        public MyViewHolder(@NonNull View itemView, onItemClickListener listener) {
            super(itemView);

            tvFullName = itemView.findViewById(R.id.tv_fullName);

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

    public void setOnItemClickListener(onItemClickListener listener){
        mListener = listener;
    }

}
