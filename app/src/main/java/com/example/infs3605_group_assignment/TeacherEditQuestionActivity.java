package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class TeacherEditQuestionActivity extends AppCompatActivity {


    private static final String TAG = "Edit ques activity";
    TextView eqShortTitle, eqQuestion, eqTime, eqDate;
    CardView eqUpdateBtn, eqDateBtn, eqTimeBtn;


    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_edit_question);

        eqShortTitle = findViewById(R.id.eq_short_title);
        eqQuestion = findViewById(R.id.eq_question);
        eqTime = findViewById(R.id.eq_time);
        eqDate = findViewById(R.id.eq_date);

        eqUpdateBtn = findViewById(R.id.eq_cv_update_btn);
        eqDateBtn = findViewById(R.id.eq_cv_date_pick);
        eqTimeBtn = findViewById(R.id.eq_cv_time_pick);

        // Retrieve relevant question data
        String postDate = getIntent().getStringExtra("postDateID");
        Log.d(TAG, "String postDate is: " + postDate);



        userRef = db.collection("Custom_Questions").document(postDate);




        userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {

                eqShortTitle.setText(documentSnapshot.get("Short_Title").toString());
                eqQuestion.setText(documentSnapshot.get("Question").toString());

                Date d = documentSnapshot.getTimestamp("Finish_Date").toDate();

                DateFormat formatterDate = new SimpleDateFormat("yyyy-MM-dd");
                DateFormat formatterTime = new SimpleDateFormat("hh:mm:ss");


                    String newFinishDate = formatterDate.format(d);
                    eqDate.setText(newFinishDate);

                    String newFinishTime = formatterTime.format(d);
                    eqTime.setText(newFinishTime);


            }
        })
        .addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d(TAG, "Set details failed");
            }
        });




        eqDateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(eqDate);
            }
        });

        eqTimeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(eqTime);
            }
        });

        eqUpdateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = eqShortTitle.getText().toString();
                Log.d(TAG, "Title is: " + title);
                String tQuestion = eqQuestion.getText().toString();
                Log.d(TAG, "Question is: " + tQuestion);

                // Check text fields are not empty
                if(title.isEmpty() || tQuestion.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Oops! Can't update quiz! One or more fields are empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String dateTime = eqDate.getText().toString() + " " + eqTime.getText().toString();
                Log.d(TAG, "Date time is: " + dateTime);

                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date newFinishDate = formatter.parse(dateTime);
                    Timestamp currentTime =  Timestamp.now();

                    // Change string intent of original post date back to date
                    //DateFormat formatter2 = new SimpleDateFormat("dd.MM.yyyy kk:mm:ss");
                    //Date originalPostDate = formatter2.parse(postDate);

                    // Check day is not before today
                    if(newFinishDate.before(currentTime.toDate())) {
                        Toast.makeText(getApplicationContext(), "Oops! Your expiration date can't be before today! Try again!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    updateCustomQuestion(title, tQuestion, newFinishDate, postDate);
                    teacher_placeholderFragment.mRecyclerView.getAdapter().notifyDataSetChanged();
                    finish();
                    Toast.makeText(getApplicationContext(), "Quiz Details Updated!", Toast.LENGTH_LONG).show();

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG, dateTime);
                }


            }
        });


    }














    public void updateCustomQuestion(String title, String question, Date finishTime, String postDateID) {


        //entry.put("Post_Date", com.google.firebase.Timestamp.now());

        userRef.update("Short_Title", title);
        userRef.update("Question", question);
        userRef.update("Finish_Date", finishTime);

    }



    private void showTimeDialog(TextView time) {
        final Calendar calendar = Calendar.getInstance();
        TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker timePicker, int i, int i1) {

                calendar.set(Calendar.HOUR_OF_DAY, i);
                calendar.set(Calendar.MINUTE, i1);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("kk:mm:ss");
                time.setText(simpleDateFormat.format(calendar.getTime()));


            }
        };
        new TimePickerDialog(TeacherEditQuestionActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();

    }




    private void showDateDialog(TextView date) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {
                calendar.set(Calendar.YEAR, i);
                calendar.set(Calendar.MONTH, i1);
                calendar.set(Calendar.DAY_OF_MONTH, i2);
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                date.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new DatePickerDialog(TeacherEditQuestionActivity.this, dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }



    // Takes timestamp data from firestore and converts to string representation, i=0 for time, i=1 for date, else whole print
    public String timestampToDate(com.google.firebase.Timestamp t, int i) {

        Date date = t.toDate();

        if (i == 0) {
            // Return just the date
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            String stringTime = formatter.format(date);
            Log.d(TAG, "Time: " + stringTime);
            return stringTime;


        } else if (i == 1) {
            // Return just the time
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            String stringTime = formatter.format(date);
            Log.d(TAG, "Date: " + stringTime);
            return stringTime;

        } else if (i == 2) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd/MMM");
            String stringTime = formatter.format(date);
            Log.d(TAG, "Date: " + stringTime);
            return stringTime;


        } else if (i == 3) {

            SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            String stringTime = formatter.format(date);
            Log.d(TAG, "Date: " + stringTime);
            return stringTime;


        } else {
            // Return the whole string date + time
            String stringTime = date.toString();
            Log.d(TAG, "Whole: " +stringTime);
            return stringTime;
        }
    }



}