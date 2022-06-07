package com.example.infs3605_group_assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.sql.Time;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class student_HomeFragment extends Fragment {

    public static final String TAG = "student_HomeFragment";


    Integer startingScore, currentLevel;
    TextView tvTitle, tvLevel;

    // Test
    // Recent log elements
    TextView rlEmojiState, rlTimeDay, rlTimeDate, rlTimeTime, rlComments, rlEmoInt;
    ImageView rlEmoji;


    RecyclerView shRecyclerView;
    ArrayList<CustomQuestionModel> shList;
    studentQuizAccessAdapter shAdapter;


    CardView cvWellbeingBtn;
    ImageView levelImage, emojiFace;
    ProgressBar progressBar;

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();

    ProgressDialog progressDialog;


    // userRef variable dives into the logged in user and retrieves all info
    DocumentReference userRef = db.collection("Users").document(fAuth.getCurrentUser().getUid());
    // test for single log entry display
    CollectionReference wellbeingLogRef = userRef.collection("Wellbeing_Log");

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        Log.d(TAG, "onCreateView called");
        return inflater.inflate(R.layout.fragment_student_home, container, false);

    }

    // This is used to refresh recent log data
    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onresume called");
        getRecentLogs();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        Log.d(TAG, "onViewCreated called");

        // Loading dialog
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();

        // Link textview elements
        tvTitle = view.findViewById(R.id.tv_logged_in_user);
        tvLevel = view.findViewById(R.id.levelTv);

        rlEmojiState = view.findViewById(R.id.sh_tv_emo_state);
        rlTimeTime = view.findViewById(R.id.sh_tv_timestamp_time);
        rlTimeDay = view.findViewById(R.id.sh_tv_timestamp_day);
        rlTimeDate = view.findViewById(R.id.sh_tv_timestamp_date);
        rlComments = view.findViewById(R.id.sh_tv_Comments);
        rlEmoInt = view.findViewById(R.id.sh_tv_emoIntensity);


        // Link cardview elements
        cvWellbeingBtn = view.findViewById(R.id.cv_option_1);

        //link ImageView elements
        levelImage = view.findViewById(R.id.levelImage);
        emojiFace = view.findViewById(R.id.sh_iv_emoji);

        //link progress bar
        progressBar = view.findViewById(R.id.progressBar);

        // Link recyclerview
        shRecyclerView = view.findViewById(R.id.sd_recyclerView);
        shRecyclerView.setHasFixedSize(true);
        shRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,true));
        shList = new ArrayList<CustomQuestionModel>();
        shAdapter = new studentQuizAccessAdapter(student_HomeFragment.this.getContext(),shList);
        shRecyclerView.setAdapter(shAdapter);
        QuestionEventChangeListener();

        // Recyclerview listener
        shAdapter.setOnQuizItemClickListener(new studentQuizAccessAdapter.OnQuizItemClickListener() {
            @Override
            public void onItemClick(int position) {
                shList.get(position);

                Date d = shList.get(position).getPost_Date().toDate();
                Log.d(TAG, "Date is: " + d);
                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String stringDate = sdf.format(d);
                Log.d(TAG, "Formatted date is: " + stringDate);

                db.collection("Custom_Questions")
                        .document(stringDate)
                        .collection("Student_Answers")
                        .get()
                        .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                            @Override
                            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                // If no student submissions received
                                if (queryDocumentSnapshots.isEmpty()) {
                                    // Check if no position selected
                                    Intent intent = new Intent(getActivity(), StudentQuizSubmissionActivity.class);
                                    intent.putExtra("postDate", stringDate);
                                    startActivity(intent);

                                    // If student submission has been received
                                } else {
                                    List<StudentCustomQuestionAnswerModel> studentAnswers = queryDocumentSnapshots.toObjects(StudentCustomQuestionAnswerModel.class);

                                    for (StudentCustomQuestionAnswerModel s : studentAnswers) {
                                        // Loop through student submissions to find matching studentID
                                        if (s.getStudent_ID().equals(fAuth.getCurrentUser().getUid())) {
                                            Toast.makeText(getActivity(), "Looks like you have already done this quiz!", Toast.LENGTH_LONG).show();
                                            return;
                                        }
                                    }
                                    Intent intent = new Intent(getActivity(), StudentQuizSubmissionActivity.class);
                                    intent.putExtra("postDate", stringDate);
                                    startActivity(intent);

                                }
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {

                            }
                        });





            }
        });


        // DB call to get user details/scores
        populateUserDetails();

        // Database call to see if wellbeing log was made today
        checkForLastLogEntry();


        // Click listener for daily wellbeing quiz
        cvWellbeingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), WellbeingQuizActivity.class);
                startActivity(intent);
            }
        });

    }

    private void QuestionEventChangeListener() {

        db.collection("Custom_Questions")
                .orderBy("Post_Date", Query.Direction.ASCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }

                        for (DocumentChange dc :value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {

                                CustomQuestionModel cqm = dc.getDocument().toObject(CustomQuestionModel.class);

                                Date expiryDate = cqm.getFinish_Date().toDate();
                                Date currentDate = Timestamp.now().toDate();

                                if (expiryDate.after(currentDate)) {
                                    shList.add(dc.getDocument().toObject(CustomQuestionModel.class));
                                }
                            }
                            shAdapter.notifyDataSetChanged();

                        }

                        Log.d(TAG, "Student home List count: " + shList.size());
                        //shRecyclerView.scrollToPosition(shList.size()-1);

                    }
                });



    }


    private void populateUserDetails() {
        // Get first name of user -> next sprint (enable getting xp, image, emoji etc
        userRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tvTitle.setText(documentSnapshot.getString("FirstName"));


                        //setting the level textView and imageView
                        startingScore = documentSnapshot.getLong("MoodProgress").intValue();
                        currentLevel = getLevel(startingScore);

                        // set progress bar
                        setProgressBar(startingScore);

                        // set emoji image drawable


                        tvLevel.setText("Lvl " + currentLevel.toString());

                        //set the image Drawable
                        switch(currentLevel){
                            case 0:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.sad_dino));
                                break;

                            case 1:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.embarrassed_dino));
                                break;

                            case 2:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.rainbow_dino));
                                break;

                            case 3:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_dino));
                                break;

                            case 4:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_dino));
                                break;

                            case 5:
                                levelImage.setImageDrawable(getResources().getDrawable(R.drawable.happy_dino));
                                break;

                        }

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(getActivity(), "Error fetching first name!", Toast.LENGTH_SHORT).show();
                        Log.d(TAG, e.toString());
                    }
                });
    }





    // METHOD: to check for last log entry
    // If no log entry today, open up log activity to make user submit a log
    // To-do: Maybe move this to an on create method so it doesnt keep popping up every time you
    // go to homescreen
    private void checkForLastLogEntry() {
        db.collection("Users")
                .document(fAuth.getCurrentUser().getUid())
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();

                        Date d = new Date();
                        SimpleDateFormat formatter = new SimpleDateFormat("dd");
                        String day = formatter.format(d);
                        String lastEntryDay;

                        for (DocumentSnapshot s : ds) {
                            Timestamp t = s.getTimestamp("Timestamp");
                            lastEntryDay = formatter.format(t.toDate());

                            if (!day.equals(lastEntryDay)) {
                                Intent intent = new Intent(getActivity(), WellbeingQuizActivity.class);
                                startActivity(intent);
                            }
                        }



                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                    }
                });

    }

    // METHOD: To display last user entered log
    private void getRecentLogs() {
        // Get latest log entry
        db.collection("Users")
                .document(fAuth.getCurrentUser().getUid())
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                        List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();


                        for (DocumentSnapshot snapshot: ds) {
                            Log.d(TAG, snapshot.getData().toString());

                            // Set comment
                            rlComments.setText(snapshot.getString("additional_comments").toString());
                            // Set intensity
                            rlEmoInt.setText(snapshot.get("intensity_of_emotion").toString());

                            // Set time attributes
                            Timestamp t = snapshot.getTimestamp("Timestamp");
                            SimpleDateFormat fDay = new SimpleDateFormat("EEEE");
                            SimpleDateFormat fDate = new SimpleDateFormat("MMM dd");
                            SimpleDateFormat fTime = new SimpleDateFormat("hh:mm a");

                            Date d = t.toDate();
                            String sfDay = fDay.format(d);
                            rlTimeDay.setText(sfDay);

                            String sfDate = fDate.format(d);
                            rlTimeDate.setText(sfDate);

                            String sfTime = fTime.format(d);
                            rlTimeTime.setText(sfTime);


                            // Set emoji
                            String emoScore = snapshot.get("emotional_rating").toString();
                            String nameOfPicture = "emoji_" + emoScore;
                            int resId = getResources().getIdentifier(nameOfPicture, "drawable", getActivity().getPackageName());
                            Drawable image = getResources().getDrawable(resId);
                            emojiFace.setImageDrawable(image);

                            // Set emo text
                            switch(Integer.parseInt(emoScore)){
                                case 1:
                                    rlEmojiState.setText("Horrible");
                                    break;

                                case 2:
                                    rlEmojiState.setText("Not Great");
                                    break;

                                case 3:
                                    rlEmojiState.setText("Ok");
                                    break;

                                case 4:
                                    rlEmojiState.setText("Pleasant");
                                    break;

                                case 5:
                                    rlEmojiState.setText("Great");
                                    break;

                            }

                        }

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }


                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {

                        if (progressDialog.isShowing()) {
                            progressDialog.dismiss();
                        }
                        Toast.makeText(getActivity(),"No Logs exist!", Toast.LENGTH_SHORT).show();

                    }
                });

    }


    public int getLevel(int score){

        int level = 0;

        if (score <= 20){ //can change these values later when we determine the levels + scoring
            level = 0;
        }else if(score > 20 && score <= 40){
            level = 1;
        }else if(score > 40 && score <= 70) {
            level = 2;
        }else if(score > 70 && score <= 100) {
            level = 3;
        }else if(score > 100 && score <= 150) {
            level = 4;
        }

        return level;
    }

    // return XP in current level
    public void setProgressBar (int currentXp) {
        if (currentXp < 20) {
            progressBar.setProgress(currentXp);
            progressBar.setMax(20);
        } else {
            // calculate remainder after divided by 20
            int xpForLevel = currentXp % 20;
            progressBar.setProgress(xpForLevel);
            progressBar.setMax(20);
        }
    }




}






/*
        // call function
        db.collection("Users")
                .document(fAuth.getCurrentUser().getUid())
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .limit(1)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        Log.d(TAG, "1. event change listener called");
                        if (error != null) {

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }


                        Log.d(TAG, "2. Retrieving");
                        for (DocumentChange dc :value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                List<DocumentSnapshot> ds = value.getDocuments();

                                String emoScore, emoInt;

                                for (DocumentSnapshot snapshot: ds) {
                                    Log.d(TAG, snapshot.getData().toString());
                                    emoScore = snapshot.get("emotional_rating").toString();
                                    emoInt = snapshot.get("intensity_of_emotion").toString();
                                    Timestamp t = snapshot.getTimestamp("Timestamp");

                                    tvEmoScore.setText("Emotion: " + emoScore);
                                    tvEmoInt.setText("Intensity: " + emoInt);
                                    tvTimeStamp.setText(timestampToDate(t, 3));
                                }

                            }
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                });


         */
