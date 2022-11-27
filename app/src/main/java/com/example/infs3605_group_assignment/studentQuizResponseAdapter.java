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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class studentQuizResponseAdapter extends RecyclerView.Adapter<studentQuizResponseAdapter.MyViewHolder> {

    private static final int VIEW_TYPE_EMPTY_LIST_PLACEHOLDER = 0;
    private static final int VIEW_TYPE_OBJECT_VIEW = 1;
    public static final String TAG = "Responses adapter";

    Context context;
    ArrayList<Model_CustomAnswers> mList;


    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    public studentQuizResponseAdapter(Context context, ArrayList<Model_CustomAnswers> mList) {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row_student_quiz_responses, parent, false);
        return new studentQuizResponseAdapter.MyViewHolder(v);

    }

    @Override
    public void onBindViewHolder(@NonNull MyViewHolder holder, int position) {

        Model_CustomAnswers cqModel = mList.get(position);
        holder.studentAnswer.setText(cqModel.getStudent_Answer());

        Date d = cqModel.getSubmission_Time().toDate();

        SimpleDateFormat sdfDayDate = new SimpleDateFormat("E, MMM dd");
        String dayDate = sdfDayDate.format(d);
        holder.sDayDate.setText(dayDate);

        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
        String timeOnly = sdfTime.format(d);
        holder.sTime.setText(timeOnly);

        String studentID = cqModel.getStudent_ID();

        db.collection("Users")
                .document(studentID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Model_Student sModel = documentSnapshot.toObject(Model_Student.class);
                        holder.sFName.setText(sModel.getFirstName());
                        holder.sLName.setText(sModel.getLastName());
                    }
                }).addOnFailureListener(new OnFailureListener() {
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

        TextView studentAnswer, sFName, sLName, sDayDate, sTime;



        public MyViewHolder(@NonNull View itemView) {
            super(itemView);

            studentAnswer = itemView.findViewById(R.id.qr_tv_student_response);
            sFName = itemView.findViewById(R.id.qr_tv_first_name);
            sLName = itemView.findViewById(R.id.qr_tv_last_name);
            sDayDate = itemView.findViewById(R.id.qr_tv_day_date);
            sTime = itemView.findViewById(R.id.qr_tv_time);




        }
    }
}
