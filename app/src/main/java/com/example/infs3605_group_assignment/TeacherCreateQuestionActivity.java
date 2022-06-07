package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firestore.v1.DocumentTransform;

import java.sql.Time;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class TeacherCreateQuestionActivity extends AppCompatActivity {


    private static final String TAG = "Create ques activity";
    EditText shortTitle, question;
    TextView date, time;
    CardView publishBtn, datePickBtn, timePickBtn;

    FirebaseFirestore db;
    FirebaseAuth fAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_create_question);

        shortTitle = (EditText) findViewById(R.id.cq_short_title);
        question = (EditText) findViewById(R.id.cq_question);
        date = findViewById(R.id.cq_date);
        time = findViewById(R.id.cq_time);
        publishBtn = findViewById(R.id.cq_cv_publish_btn);

        datePickBtn = findViewById(R.id.cq_cv_date_btn);
        timePickBtn = findViewById(R.id.cq_cv_time_btn);

        date.setInputType(InputType.TYPE_NULL);
        time.setInputType(InputType.TYPE_NULL);


        db = FirebaseFirestore.getInstance();
        fAuth = FirebaseAuth.getInstance();


        datePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDateDialog(date);
            }
        });

        timePickBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showTimeDialog(time);
            }
        });

        publishBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String title = shortTitle.getText().toString();
                Log.d(TAG, "Title is: " + title);
                String tQuestion = question.getText().toString();
                Log.d(TAG, "Question is: " + tQuestion);

                // Check text fields are not empty
                if(title.isEmpty() || tQuestion.isEmpty()) {
                    Toast.makeText(getApplicationContext(), "Oops! Can't create question! One or more fields are empty!", Toast.LENGTH_LONG).show();
                    return;
                }

                String dateTime = date.getText().toString() + " " + time.getText().toString();
                Log.d(TAG, "Date time is: " + dateTime);

                // Check to make sure date and time has been selected
                if(dateTime.equals("Select Date Select Time")) {
                    Toast.makeText(getApplicationContext(), "Oops! Please select a date and time!", Toast.LENGTH_LONG).show();
                    return;
                }

                try {
                    DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                    Date d = formatter.parse(dateTime);
                    Timestamp currentTime =  Timestamp.now();

                    // Check day is not before today
                    if(d.before(currentTime.toDate())) {
                        Toast.makeText(getApplicationContext(), "Oops! Your expiration date can't be before today! Try again!", Toast.LENGTH_LONG).show();
                        return;
                    }

                    saveCustomQuestion(title, tQuestion, d, currentTime);

                } catch (ParseException e) {
                    e.printStackTrace();
                    Log.d(TAG, dateTime);
                }


            }
        });


    }



    public void saveCustomQuestion(String title, String question, Date finishTime, com.google.firebase.Timestamp submitTime) {


        // Map to add wellbeing log into specific collection
        Map<String, Object> entry = new HashMap<>();
        entry.put("Short_Title", title);
        entry.put("Question", question);
        entry.put("Post_Date", com.google.firebase.Timestamp.now());
        entry.put("Finish_Date", finishTime);
        entry.put("Teacher_ID", fAuth.getCurrentUser().getUid());

        db
                .collection("Custom_Questions")
                .document(timestampToDate(submitTime, 3))
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(TeacherCreateQuestionActivity.this, "Question added!", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(TeacherCreateQuestionActivity.this, "Question adding failed! Please try again!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });

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
        new TimePickerDialog(TeacherCreateQuestionActivity.this, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE), true).show();

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

        new DatePickerDialog(TeacherCreateQuestionActivity.this, dateSetListener, calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }










}