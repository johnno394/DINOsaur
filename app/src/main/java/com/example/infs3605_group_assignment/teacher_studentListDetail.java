package com.example.infs3605_group_assignment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class teacher_studentListDetail extends AppCompatActivity {


    String studentUid;

    RecyclerView recyclerView;
    ArrayList<Model_WellbeingEntry> mList;
    studentEntryLogAdapter mAdapter;
    CardView toGraphBtn, emailBtn;
    TextView detailDesc;
    ImageView backButton;

    ProgressDialog progressDialog;

    FirebaseFirestore db;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public static final String TAG = "studentListDetail";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_list_detail);

        toGraphBtn = findViewById(R.id.cv_graphs);
        detailDesc = findViewById(R.id.detailDesc);
        backButton = findViewById(R.id.imageView7);
        emailBtn = findViewById(R.id.cv_email);


        recyclerView = findViewById(R.id.recyclerView2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        mList = new ArrayList<Model_WellbeingEntry>();


        db = FirebaseFirestore.getInstance();
        mAdapter = new studentEntryLogAdapter(this,mList);

        recyclerView.setAdapter(mAdapter);

        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();


        Intent intent= getIntent();

        studentUid = intent.getStringExtra("student Uid");
        EventChangeListener();



        String teacherUID = fAuth.getCurrentUser().getUid();
        Log.d(TAG, teacherUID);

        DocumentReference docRef = db.collection("Users").document(studentUid);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                detailDesc.setText(documentSnapshot.getString("FirstName") + "'s Mood History");


            }
        });


        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent (teacher_studentListDetail.this, TeacherDashActivity.class));
            }
        });


        toGraphBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(teacher_studentListDetail.this, teacher_studentListGraph.class);
                i.putExtra("UID", studentUid);
                startActivity(i);
            }
        });

        emailBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        Intent emailintent = new Intent(Intent.ACTION_SEND);
                        emailintent.setType("plain/text");
                        emailintent.putExtra(Intent.EXTRA_EMAIL, new String[] { documentSnapshot.getString("Parents Email") });
                        emailintent.putExtra(Intent.EXTRA_SUBJECT, documentSnapshot.getString("FirstName" + "'s Emotional Wellbeing"));
                        emailintent.putExtra(Intent.EXTRA_TEXT, "Hi, I think we should have a talk about your child " + documentSnapshot.getString("FirstName")
                                + ", they do not seem to be having the best time emotionally right now.");
                        startActivity(Intent.createChooser(emailintent, ""));



                    }
                });




            }
        });



    }


    private void EventChangeListener() {

        db.collection("Users")
                .document(studentUid)
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }

                        if (value.isEmpty()) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "No logs to show!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc :value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                mList.add(dc.getDocument().toObject(Model_WellbeingEntry.class));
                            }
                            mAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                });


    }

}