package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.slider.Slider;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

public class WellbeingQuizActivity extends AppCompatActivity {


    private static final String TAG = "Wellbeing quiz activity";

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    DocumentReference userRef = db.collection("Users").document(fAuth.getCurrentUser().getUid());

    ImageView dailyQuizImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wellbeing_quiz);

        Slider ratingSlider1, ratingSlider2;
        TextView question;
        EditText userComments;
        Button submitBtn;



        // Link XML elements
        ratingSlider1 = findViewById(R.id.slider);
        ratingSlider2 = findViewById(R.id.slider2);
        question = findViewById(R.id.tvQuestion);
        submitBtn = findViewById(R.id.button);
        userComments = findViewById(R.id.etComments);

        dailyQuizImage = findViewById(R.id.dailyQuizImage);



        ratingSlider1.addOnChangeListener(new Slider.OnChangeListener() {
            @Override
            public void onValueChange(@NonNull Slider slider, float value, boolean fromUser) {
                //Use the value
                Log.d("value", String.valueOf(value));

                int intValue = (int) value;

                switch(intValue){
                    case 1:
                        dailyQuizImage.setImageDrawable(getResources().getDrawable(R.drawable.emoji_1));
                        break;

                    case 2:
                        dailyQuizImage.setImageDrawable(getResources().getDrawable(R.drawable.emoji_2));
                        break;

                    case 3:
                        dailyQuizImage.setImageDrawable(getResources().getDrawable(R.drawable.emoji_3));
                        break;

                    case 4:
                        dailyQuizImage.setImageDrawable(getResources().getDrawable(R.drawable.emoji_4));
                        break;

                    case 5:
                        dailyQuizImage.setImageDrawable(getResources().getDrawable(R.drawable.emoji_5));
                        break;


                }


        }
        });





        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                int q1Answer = (int) ratingSlider1.getValue();
                int q2Answer = (int) ratingSlider2.getValue();
                String comment = userComments.getText().toString();
                Timestamp t = Timestamp.now();





                saveEmotionalEntry(q1Answer,q2Answer,comment, t);

            }
        });



    }


    public void saveEmotionalEntry(int emoScore, int emoInt, String comments, Timestamp time) {


        // Map to add wellbeing log into specific collection
        Map<String,Object> entry = new HashMap<>();
        entry.put("emotional_rating",emoScore);
        entry.put("intensity_of_emotion", emoInt);
        entry.put("Timestamp", time);
        entry.put("additional_comments", comments);
        entry.put("userID", fAuth.getCurrentUser().getUid());

        userRef
                .collection("Wellbeing_Log")
                .document(timestampToDate(time,3))
                .set(entry)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void unused) {
                        Toast.makeText(WellbeingQuizActivity.this, "Success!", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(WellbeingQuizActivity.this, "Failed! Please try again!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });




        // Update latest entries to add wellbeing log into user values: latest entry
        userRef.update("latest_emotional_rating",emoScore);
        userRef.update("latest_intensity_of_emotion", emoInt);
        userRef.update("latest_Timestamp", time);
        userRef.update("latest_additional_comments", comments);





    }


    // Takes timestamp data from firestore and converts to string representation, i=0 for time, i=1 for date, else whole print
    public String timestampToDate(Timestamp t, int i) {

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



















