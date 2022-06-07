package com.example.infs3605_group_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class teacherCustomQuestionsAdapter extends RecyclerView.Adapter<teacherCustomQuestionsAdapter.MyViewHolder> {

    Context context;
    ArrayList<CustomQuestionModel> mList;
    public static final String TAG = "CustomQuestionAdapter";
    private OnItemClickListener mListener;
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    public interface OnItemClickListener {
        void onItemClick(int position);
    }

    public void setOnItemClickListener(OnItemClickListener listener) {
        mListener = listener;
    }

    public teacherCustomQuestionsAdapter(Context context, ArrayList<CustomQuestionModel> mList) {
        this.context = context;
        this.mList = mList;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row_teacher_questions, parent, false);

        return new MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        CustomQuestionModel cModel = mList.get(position);

        holder.question.setText(cModel.getQuestion().toString());
        holder.shortTitle.setText(cModel.getShort_Title().toString());

        Timestamp t = cModel.getPost_Date();
        SimpleDateFormat fDayDate = new SimpleDateFormat("EEE, MMM dd");
        SimpleDateFormat fYear = new SimpleDateFormat("yyyy");
        Date d = t.toDate();
        holder.postDayDate.setText(fDayDate.format(d));
        holder.postYear.setText(fYear.format(d));

        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String stringTime = formatter.format(cModel.getPost_Date().toDate());



        // Bind response count
        db.collection("Custom_Questions")
                .document(stringTime)
                .collection("Student_Answers")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        int responseCount = 0;
                        if (queryDocumentSnapshots.isEmpty()) {
                            holder.responseCount.setText(String.valueOf(responseCount));
                            return;
                        }
                        List<DocumentSnapshot> doc = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : doc) {
                            responseCount++;
                        }
                        holder.responseCount.setText(String.valueOf(responseCount));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });


    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {

        TextView question, shortTitle, responseCount, postDayDate, postYear;

        public MyViewHolder(@NonNull View itemView, OnItemClickListener listener) {
            super(itemView);

            question = itemView.findViewById(R.id.qh_tv_full_question);
            shortTitle = itemView.findViewById(R.id.qh_tv_short_title);
            responseCount = itemView.findViewById(R.id.qh_tv_response_count);
            postDayDate = itemView.findViewById(R.id.qh_tv_timestamp_post_day_date);
            postYear = itemView.findViewById(R.id.qh_tv_timestamp_post_year);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    if (listener != null) {
                        int position = getAdapterPosition();
                        if (position != RecyclerView.NO_POSITION) {
                            listener.onItemClick(position);
                        }
                    }
                }
            });


        }
    }
}
