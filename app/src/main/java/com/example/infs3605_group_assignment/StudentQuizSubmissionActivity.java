package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class StudentQuizSubmissionActivity extends AppCompatActivity {

    TextView questionTitle;
    EditText studentAnswer;
    CardView submitButton;

    public static final String TAG = "StudentQuizSubmission";

    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_quiz_submission);

        questionTitle = findViewById(R.id.sq_question);
        studentAnswer = findViewById(R.id.sq_studentAnswer);
        submitButton = findViewById(R.id.sq_submitButton);

        // Retrieve relevant question data
        String postDate = getIntent().getStringExtra("postDate");
        Log.d(TAG, "String postDate is: " + postDate);


        getQuestionData(postDate);

        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String stringAnswer = studentAnswer.getText().toString();

                if (stringAnswer.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Oops! Looks like you haven't typed anything!", Toast.LENGTH_SHORT).show();
                    return;
                }

                Timestamp t = Timestamp.now();
                addStudentAnswerToQuiz(postDate, stringAnswer, t);

            }
        });




    }


    private void addStudentAnswerToQuiz(String dateID, String sAnswer, Timestamp submissionTime) {


        // Format to create document with unique time string id
        /*
        Date d = submissionTime.toDate();
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
        String formattedStringDate = sdf.format(d);
        Log.d(TAG, "Formatted date is: " + formattedStringDate);

         */

        // Map to add wellbeing log into specific collection
        Map<String,Object> entry = new HashMap<>();
        entry.put("Submission_Time",submissionTime);
        entry.put("Student_ID", fAuth.getCurrentUser().getUid());
        entry.put("Student_Answer", sAnswer);
        entry.put("Has_Submitted", true);

        db.collection("Custom_Questions")
                .document(dateID)
                .collection("Student_Answers")
                //.document(formattedStringDate)
                .document(fAuth.getCurrentUser().getUid())
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(getApplicationContext(), "Your answer has been recorded!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getApplicationContext(), "Oops! Something went wrong, try again!", Toast.LENGTH_SHORT).show();
                    }
                });
    }


    private void getQuestionData(String dateID) {

        Log.d(TAG, "getting question data");
        FirebaseFirestore data = FirebaseFirestore.getInstance();
        data
                .collection("Custom_Questions")
                .document(dateID)
                .get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {

                        CustomQuestionModel c = documentSnapshot.toObject(CustomQuestionModel.class);
                        questionTitle.setText(c.getQuestion());

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