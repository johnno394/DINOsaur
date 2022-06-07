package com.example.infs3605_group_assignment;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;

public class student_PlaceholderFragment extends Fragment {


    public FirebaseFirestore fStore;
    public FirebaseAuth fAuth;

    String moodToday;
    Integer startingScore, currentScore;
    TextView question, quizDesc;
    List<MoodQuestion> moodQuestionsList = new ArrayList<>();
    Integer currentQuestionPosition = 0;
    ImageView quizHomeImage, backBtn;


    Button option1Btn, option2Btn, option3Btn, option4Btn, startQuizBtn;




    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_placeholder, container, false);


    }


    @Override
    public void onViewCreated(View view, Bundle savedInstanceState){


        fAuth = FirebaseAuth.getInstance();
        fStore = FirebaseFirestore.getInstance();


        //set the current score for this quiz session to 0
        currentScore = 0;

        //Link XML
        option1Btn = view.findViewById(R.id.option1Btn);
        option2Btn = view.findViewById(R.id.option2Btn);
        option3Btn = view.findViewById(R.id.option3Btn);
        option4Btn = view.findViewById(R.id.option4Btn);
        startQuizBtn = view.findViewById(R.id.startQuizBtn);
        question = view.findViewById(R.id.questionTV);
        quizHomeImage = view.findViewById(R.id.imageView6);
        quizDesc = view.findViewById(R.id.quizDesc);
        backBtn = view.findViewById(R.id.imageView8);



        //Set question buttons to invisible on creation of the view
        option1Btn.setVisibility(View.GONE);
        option2Btn.setVisibility(View.GONE);
        option3Btn.setVisibility(View.GONE);
        option4Btn.setVisibility(View.GONE);
        question.setVisibility(View.GONE);
        startQuizBtn.setVisibility(View.VISIBLE);
        quizHomeImage.setVisibility(View.VISIBLE);
        quizDesc.setVisibility(View.VISIBLE);
        backBtn.setVisibility(View.GONE);




        DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());
        df.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                //get the students starting mood score (progress)
                startingScore = documentSnapshot.getLong("MoodProgress").intValue();

            }
        });


        moodQuestionsList = MoodQuestion.getMoodQuestions();

        startQuizBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startQuizBtn.setVisibility(View.GONE);
                option1Btn.setVisibility(View.VISIBLE);
                option2Btn.setVisibility(View.VISIBLE);
                option3Btn.setVisibility(View.VISIBLE);
                option4Btn.setVisibility(View.VISIBLE);
                question.setVisibility(View.VISIBLE);
                backBtn.setVisibility(View.VISIBLE);
                quizHomeImage.setVisibility(View.GONE);
                quizDesc.setVisibility(View.GONE);



                question.setText(moodQuestionsList.get(0).getQuestion());
                option1Btn.setText(moodQuestionsList.get(0).getOption1());
                option2Btn.setText(moodQuestionsList.get(0).getOption2());
                option3Btn.setText(moodQuestionsList.get(0).getOption3());
                option4Btn.setText(moodQuestionsList.get(0).getOption4());

            }
        });


        option1Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                changeNextQuestion();


            }
        });

        option2Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentScore = currentScore + 2;
                changeNextQuestion();


            }
        });

        option3Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentScore = currentScore + 5;
                changeNextQuestion();


            }
        });

        option4Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                currentScore = currentScore + 10;
                changeNextQuestion();


            }
        });


        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                startActivity(new Intent(getActivity(), StudentDashActivity.class));
            }
        });



    }


    private void changeNextQuestion() {

        currentQuestionPosition++;
        DocumentReference df = fStore.collection("Users").document(fAuth.getCurrentUser().getUid());



        if (currentQuestionPosition < moodQuestionsList.size()) {

            question.setText(moodQuestionsList.get(currentQuestionPosition).getQuestion());
            option1Btn.setText(moodQuestionsList.get(currentQuestionPosition).getOption1());
            option2Btn.setText(moodQuestionsList.get(currentQuestionPosition).getOption2());
            option3Btn.setText(moodQuestionsList.get(currentQuestionPosition).getOption3());
            option4Btn.setText(moodQuestionsList.get(currentQuestionPosition).getOption4());

        }else if(currentQuestionPosition == moodQuestionsList.size()){

            option1Btn.setClickable(false);
            option2Btn.setClickable(false);


            Intent intent = new Intent(getActivity(), QuizResultsActivity.class);
            intent.putExtra("Quiz Score", currentScore); //can send an intent with how they scored in the quiz

            startingScore = startingScore + currentScore;

            df.update("MoodProgress", startingScore);
            startActivity(intent);


        }





    }












}