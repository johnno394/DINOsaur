package com.example.infs3605_group_assignment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

public class studentQuizAccessAdapter extends RecyclerView.Adapter<studentQuizAccessAdapter.MyViewHolder> {

    Context context;
    ArrayList<Model_CustomQuestion> mList;
    public static final String TAG = "studQuizAccessAdapter";
    private OnQuizItemClickListener mListener;

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public interface OnQuizItemClickListener {
        void onItemClick(int position);
    }

    public void setOnQuizItemClickListener(OnQuizItemClickListener listener) {
        mListener = listener;
    }


    public studentQuizAccessAdapter(Context context, ArrayList<Model_CustomQuestion> mList) {
        this.context = context;
        this.mList = mList;
    }


    @NonNull
    @Override
    public studentQuizAccessAdapter.MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View v = LayoutInflater.from(context).inflate(R.layout.item_list_row_student_view_questions, parent, false);

        return new studentQuizAccessAdapter.MyViewHolder(v, mListener);
    }

    @Override
    public void onBindViewHolder(@NonNull studentQuizAccessAdapter.MyViewHolder holder, int position) {

        Model_CustomQuestion cModel = mList.get(position);
        holder.qShortTitle.setText(cModel.getShort_Title());

        Date d = cModel.getPost_Date().toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String stringDate = sdf.format(d);

        Timestamp expiryDate = cModel.getFinish_Date();
        holder.qExpiryCountdown.setText(CalculateTimeTillExpiry(expiryDate));


        db.collection("Custom_Questions")
                .document(stringDate)
                .collection("Student_Answers")
                .document(fAuth.getCurrentUser().getUid())
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        if (documentSnapshot.exists()) {
                            Model_CustomAnswers sModel = documentSnapshot.toObject(Model_CustomAnswers.class);
                            Boolean entryExists = sModel.getHas_Submitted();

                            if (entryExists) {
                                holder.qStudentCompleteBox.setImageDrawable(context.getDrawable(R.drawable.tick));
                            } else {
                                holder.qStudentCompleteBox.setImageDrawable(context.getDrawable(R.drawable.exclaim));
                            }

                        } else {
                            holder.qStudentCompleteBox.setImageDrawable(context.getDrawable(R.drawable.exclaim));
                        }


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

        TextView qShortTitle;
        ImageView qStudentCompleteBox;
        TextView qExpiryCountdown;

        public MyViewHolder(@NonNull View itemView, OnQuizItemClickListener listener) {
            super(itemView);

            qShortTitle = itemView.findViewById(R.id.q_tv_shortTitle);
            qStudentCompleteBox = itemView.findViewById(R.id.q_img_tickbox);
            qExpiryCountdown = itemView.findViewById(R.id.q_expiry_countdown);

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

    private String CalculateTimeTillExpiry(Timestamp t) {

        Date now = new Date();
        Date expiry = t.toDate();
        long timeLeft = TimeUnit.MILLISECONDS.toMillis(expiry.getTime() - now.getTime());

        if (timeLeft < 60000) {
            //elapsed time between 1 second - 59 sec
            long secTime = TimeUnit.MILLISECONDS.toMinutes(expiry.getTime() - now.getTime());
            return "Expires in less than " + String.valueOf(secTime) + " minute!";
        } else if (timeLeft >= 60000 && timeLeft < 3600000) {
            // Elapsed time between 1 min - 59min
            long minTime = TimeUnit.MILLISECONDS.toMinutes(expiry.getTime() - now.getTime());
            int mins = (int) minTime;
            if (mins == 1) {
                return "Expires in " + String.valueOf(minTime) + " minute";
            } else {
                return "Expires in " + String.valueOf(minTime) + " minutes";
            }
        } else {
            long hrTime = TimeUnit.MILLISECONDS.toHours(expiry.getTime() - now.getTime());
            int time = (int) hrTime;
            if (time >= 24) {

                long dayTime = TimeUnit.MILLISECONDS.toDays(expiry.getTime() - now.getTime());
                int days = (int) dayTime;
                if (days == 1) {
                    return "Expires in " + String.valueOf(days) + " day";
                } else {
                    return "Expires in " + String.valueOf(days) + " days";
                }

            } else if (time == 1) {
                return "Expires in " + String.valueOf(hrTime) + " hour";
            } else {
                return "Expires in " + String.valueOf(hrTime) + " hours";
            }
        }
    }

}

