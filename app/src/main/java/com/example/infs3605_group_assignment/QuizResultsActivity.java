package com.example.infs3605_group_assignment;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

public class QuizResultsActivity extends AppCompatActivity {


    Button backHomeBtn;
    Integer dailyQuizScore;

    TextView resultsTv, adviceTv;
    ImageView resultsImage;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_quiz_results);

        dailyQuizScore = getIntent().getIntExtra("Quiz Score", 0);


        resultsTv = findViewById(R.id.resutlsTV);
        adviceTv = findViewById(R.id.adviceTV);
        resultsImage = findViewById(R.id.resultsImage);



        //if statements for different score ranges change text and image dynamically

        if (dailyQuizScore < 20){

            resultsTv.setText("It looks like you aren't feeling too happy");
            adviceTv.setText("You should speak to a teacher or your parents");
            resultsImage.setImageDrawable(getResources().getDrawable(R.drawable.sad_dino));


        }else if (dailyQuizScore >= 20 && dailyQuizScore < 30){

            resultsTv.setText("Are you feeling alright at School?");
            adviceTv.setText("You should speak to a teacher or your parents if you are having any issues");
            resultsImage.setImageDrawable(getResources().getDrawable(R.drawable.embarrassed_dino));

        }else if(dailyQuizScore >= 30 && dailyQuizScore < 40){

            resultsTv.setText("You are in a good mental state");
            adviceTv.setText("Hooray! Keep working on being happy");
            resultsImage.setImageDrawable(getResources().getDrawable(R.drawable.rainbow_dino));


        }else if (dailyQuizScore >= 40){

            resultsTv.setText("Wow you are in a great mental state!");
            adviceTv.setText("Continue living you life");
            resultsImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_dino));



        }





        //button to go back to student dashboard
        backHomeBtn = findViewById(R.id.backHomeBtn);

        backHomeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(QuizResultsActivity.this, StudentDashActivity.class));
            }
        });





    }
}