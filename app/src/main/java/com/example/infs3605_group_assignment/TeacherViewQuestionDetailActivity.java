package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;

public class TeacherViewQuestionDetailActivity extends AppCompatActivity {

    private static final String TAG = "QuestionDetailAct";
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    TextView datePosted, shortTitle, titleShortTitle, question, expiryDate, expiryTime;
    CardView editBtn;

    RecyclerView qdRecyclerView;
    ArrayList<Model_CustomAnswers> qdList;
    studentQuizResponseAdapter qdAdapter;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_view_question_detail);

        // This activity is gonna be for teachers to view quiz details and responses (connect to
        // recyclerview on click listener


        datePosted = findViewById(R.id.qd_tv_date_posted);
        shortTitle = findViewById(R.id.qd_tv_short_title);
        question = findViewById(R.id.qd_tv_question);
        expiryDate = findViewById(R.id.qd_tv_expiry_date);
        expiryTime = findViewById(R.id.qd_tv_expiry_time);
        editBtn = findViewById(R.id.qd_cv_edit_btn);
        titleShortTitle = findViewById(R.id.qd_tv_title);

        // Retrieve relevant question data
        String postDate = getIntent().getStringExtra("postDate");
        Log.d(TAG, "String postDate is: " + postDate);


        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), TeacherEditQuestionActivity.class);
                intent.putExtra("postDateID", postDate);
                startActivity(intent);
                finish();
            }
        });


        qdList = new ArrayList<Model_CustomAnswers>();
        qdAdapter = new studentQuizResponseAdapter(getApplicationContext(),qdList);

        // Link recyclerview
        Log.d(TAG, " count is: " + qdAdapter.getItemCount());
        qdRecyclerView = findViewById(R.id.qd_rv_responses);
        qdRecyclerView.setHasFixedSize(true);
        qdRecyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false));


        qdRecyclerView.setAdapter(qdAdapter);

        ResponsesEventChangeListener(postDate);


    }


    @Override
    protected void onResume() {
        super.onResume();

        Log.d(TAG, "Onresume called");
        // Retrieve relevant question data
        String postDate = getIntent().getStringExtra("postDate");
        Log.d(TAG, "String postDate is: " + postDate);
        getQuestionData(postDate);


    }

    private void ResponsesEventChangeListener(String postID) {

        db.collection("Custom_Questions")
                .document(postID)
                .collection("Student_Answers")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Toast.makeText(getApplicationContext(), "Firestore error!", Toast.LENGTH_SHORT).show();
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }

                        if (value.isEmpty()) {
                            Toast.makeText(getApplicationContext(), "No students have responded", Toast.LENGTH_SHORT).show();
                            return;
                        }
                        for (DocumentChange dc :value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                Model_CustomAnswers cqm = dc.getDocument().toObject(Model_CustomAnswers.class);
                                qdList.add(dc.getDocument().toObject(Model_CustomAnswers.class));

                            }
                            qdAdapter.notifyDataSetChanged();

                        }

                        Log.d(TAG, "Responses List count: " + qdList.size());
                        //shRecyclerView.scrollToPosition(shList.size()-1);

                    }
                });



    }




    private void getQuestionData(String dateID) {

        Log.d(TAG, "Onresume called -> get question data");
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data
                .collection("Custom_Questions")
                .document(dateID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        Model_CustomQuestion c = documentSnapshot.toObject(Model_CustomQuestion.class);

                        SimpleDateFormat sdf = new SimpleDateFormat("EEE MMM dd, yyyy");
                        datePosted.setText("Posted " + sdf.format(c.getPost_Date().toDate()));

                        shortTitle.setText(c.getShort_Title());
                        titleShortTitle.setText(c.getShort_Title());
                        question.setText(c.getQuestion());

                        SimpleDateFormat sdfDate = new SimpleDateFormat("dd/MM/yyyy");
                        SimpleDateFormat sdfTime = new SimpleDateFormat("hh:mm a");
                        expiryDate.setText(sdfDate.format(c.getFinish_Date().toDate()));
                        expiryTime.setText(sdfTime.format(c.getFinish_Date().toDate()));


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "on failure listener invoked ");
                        Log.d(TAG, "There was an error " + e.toString());
                    }
                });


    }


}