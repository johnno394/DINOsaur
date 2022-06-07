package com.example.infs3605_group_assignment;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.anychart.APIlib;
import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.charts.Scatter;
import com.anychart.core.scatter.series.Marker;
import com.anychart.enums.Anchor;
import com.anychart.enums.Position;
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

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class teacher_HomeFragment extends Fragment {


    public static final String TAG = "student_HomeFragment";

    TextView tLoggedInUser;
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    DocumentReference docRef = db.collection("Users").document(fAuth.getCurrentUser().getUid());

    List<StudentModel> sGraphList = new ArrayList<StudentModel>();

    AnyChartView anyChartView;

    RecyclerView sRecyclerView;
    ArrayList<StudentModel> sCommentsList;
    teacherHomeCommentsAdapter sAdapter;


    AlertDialog.Builder dialogBuilder;
    AlertDialog dialog;
    TextView sName, sComment, sEmotion, sTimePosted;
    ProgressBar sIntensity;
    ImageView sEmoImg, sExitBtn;

    ProgressDialog progressDialog;



    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View v = inflater.inflate(R.layout.fragment_teacher__home, container, false);

        anyChartView = v.findViewById(R.id.teacher_anyChart);

        // Loading dialog
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();


        db.collection("Users")
                .whereEqualTo("isTeacher", "0")
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> ds = queryDocumentSnapshots.getDocuments();

                        for (DocumentSnapshot d : ds) {


                            if(!d.get("latest_emotional_rating").toString().equals("0")) {
                                sGraphList.add(d.toObject(StudentModel.class));
                                Log.d(TAG, "Adding ");
                                Log.d(TAG, d.get("FirstName").toString());
                                Log.d(TAG, d.get("LastName").toString());
                                Log.d(TAG, d.get("Email").toString());
                                Log.d(TAG, d.get("MoodProgress").toString());
                                Log.d(TAG, d.get("isTeacher").toString());

                                Log.d(TAG, d.get("latest_Timestamp").toString());
                                Log.d(TAG, d.get("latest_additional_comments").toString());
                                Log.d(TAG, String.valueOf(d.get("latest_emotional_rating")));
                                Log.d(TAG, String.valueOf(d.get("latest_intensity_of_emotion")));
                            }


                        }
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {

                        Scatter quadrant = AnyChart.quadrant();

                        quadrant.padding(35d, 35d, 35d, 35d);
                        quadrant.yScale().minimum(0);
                        quadrant.yScale().maximum(6);
                        quadrant.xScale().minimum(0);
                        quadrant.xScale().maximum(6);
                        //quadrant.background().cornerType("round").corners(10);
                        quadrant.background().fill("#f8fcfc");

                        List<DataEntry> seriesData = new ArrayList<>();

                        for (StudentModel sm : sGraphList) {

                            seriesData.add(new QuadrantDataEntry(sm.getLatest_emotional_rating(), sm.getLatest_intensity_of_emotion(), sm.getFirstName()));
                            Log.d(TAG, "Added another entry");
                        }

                        Marker marker = quadrant.marker(seriesData);
                        marker.labels()
                                .enabled(true)
                                .fontFamily("sans-serif")
                                .fontColor("#4B4B4B");
                        marker.labels()
                                .position("right")
                                .anchor(Anchor.LEFT_CENTER)
                                .offsetX(5d)
                                .offsetY(0d)
                                .format("{%Name}");
                        marker.tooltip(true);

                        quadrant.quarters(
                                "{\n" +
                                        "      rightTop: {\n" +
                                        "        fill: '#f8fcfc',\n" +
                                        "        title: {\n" +
                                        "          text: 'DOING WELL',\n" +
                                        "          fontColor: '#4B4B4B',\n" +
                                        "          fontFamily: 'sans-serif',\n" +
                                        "          padding: 10\n" +
                                        "        }\n" +
                                        "      },\n" +
                                        "      rightBottom: {\n" +
                                        "        fill: '#f8fcfc',\n" +
                                        "        title: {\n" +
                                        "          text: 'OK',\n" +
                                        "          fontColor: '#4B4B4B',\n" +
                                        "          padding: 10\n" +
                                        "        }\n" +
                                        "      },\n" +
                                        "      leftTop: {\n" +
                                        "        fill: '#f8fcfc',\n" +
                                        "        title: {\n" +
                                        "          text: 'CONCERNS',\n" +
                                        "          fontColor: '#4B4B4B',\n" +
                                        "          padding: 10\n" +
                                        "        }\n" +
                                        "      },\n" +
                                        "      leftBottom: {\n" +
                                        "        fill: '#f8fcfc',\n" +
                                        "        title: {\n" +
                                        "          text: 'MONITOR',\n" +
                                        "          fontColor: '#4B4B4B',\n" +
                                        "          padding: 10\n" +
                                        "        }\n" +
                                        "      }\n" +
                                        "    }");

                        com.anychart.core.ui.Label label = quadrant.quarters().leftBottom().label(0d);
                        label.enabled(true);
                        label.useHtml(true);
                        label.position(Position.LEFT_BOTTOM);
                        label.anchor(Anchor.LEFT_CENTER);

                        label.offsetX(-20d);
                        label.text("Intensity of Emotion");
                        label.rotation(-90d);

                        label = quadrant.quarters().leftBottom().label(1d);
                        label.enabled(true);
                        label.useHtml(true);
                        label.position(Position.LEFT_BOTTOM);
                        label.anchor(Anchor.LEFT_CENTER);
                        label.offsetY(20d);
                        label.text("Emotional Rating");

                        anyChartView.setChart(quadrant);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        }, 2000);
                    }
                });


        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        // Instantiate view elements
        tLoggedInUser = view.findViewById(R.id.tv_logged_in_teacher);


        // Query call for logged in user first + last name
        docRef.get()
                .addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        tLoggedInUser.setText(documentSnapshot.getString("FirstName"));
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fetching teacher data failed");
                    }
                });

        sRecyclerView = view.findViewById(R.id.rv_comments);
        sRecyclerView.setHasFixedSize(true);
        sRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(),LinearLayoutManager.HORIZONTAL,false));



        db = FirebaseFirestore.getInstance();
        sCommentsList = new ArrayList<StudentModel>();
        sAdapter = new teacherHomeCommentsAdapter(teacher_HomeFragment.this.getContext(),sCommentsList);

        sAdapter.setOnCommentClickListener(new teacherHomeCommentsAdapter.OnCommentClickListener() {
            @Override
            public void onCommentClick(int position) {
                sCommentsList.get(position);

                String name = sCommentsList.get(position).getFirstName() + " " + sCommentsList.get(position).getLastName();
                String comment = sCommentsList.get(position).getLatest_additional_comments();
                Timestamp t = sCommentsList.get(position).getLatest_Timestamp();
                String timeElapsed = "Posted " + CalculateTimeSincePosted(t);
                int intensity = sCommentsList.get(position).getLatest_intensity_of_emotion().intValue();
                int emotion = sCommentsList.get(position).getLatest_emotional_rating().intValue();




                Log.d(TAG, "List for comments is: " + sCommentsList.size());
                Log.d(TAG, "List for comments is: " + sGraphList.size());
                Log.d(TAG, "Position is: " + position);
                createCommentDetailDialog(name, comment, timeElapsed, intensity, emotion);
            }
        });


        sRecyclerView.setAdapter(sAdapter);
        //sRecyclerView.getLayoutManager().scrollToPosition(sAdapter.getItemCount()-1);




        commentEventChangeListener();





    }


    private void commentEventChangeListener() {

        db.collection("Users")
                .orderBy("latest_Timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }

                        for (DocumentChange dc :value.getDocumentChanges()) {

                            // Run check to make sure user has not just regoed (has not submitted a mood entry yet
                            if(dc.getDocument().toObject(StudentModel.class).getLatest_emotional_rating() != 0) {
                                if (dc.getType() == DocumentChange.Type.ADDED) {
                                    sCommentsList.add(dc.getDocument().toObject(StudentModel.class));
                                }
                            }

                            sAdapter.notifyDataSetChanged();
                        }
                    }
                });


    }




    public void createCommentDetailDialog(String name, String comment, String time, int intensity, int emotion) {
        dialogBuilder = new AlertDialog.Builder(this.getContext());
        final View commentDetailPopup = getLayoutInflater().inflate(R.layout.popup_teacher_comments_detail, null);
        sName = commentDetailPopup.findViewById(R.id.pup_tv_name_of_student);
        sComment = commentDetailPopup.findViewById(R.id.pup_tv_student_comment);
        sEmotion = commentDetailPopup.findViewById(R.id.pup_tv_dino_feeling);
        sIntensity = commentDetailPopup.findViewById(R.id.pup_prog_bar_intensity);
        sEmoImg = commentDetailPopup.findViewById(R.id.pup_iv_emo_img);
        sExitBtn = commentDetailPopup.findViewById(R.id.pup_exit_btn);
        sTimePosted = commentDetailPopup.findViewById(R.id.pup_tv_posted_time);


        sName.setText(name);
        sComment.setText(comment);
        sTimePosted.setText(time);
        sIntensity.setProgress(intensity);
        sIntensity.setMax(5);

        switch(emotion){
            case 1:
                sEmotion.setText("Feeling Horrible");
                sEmoImg.setImageDrawable(getResources().getDrawable(R.drawable.emoji_1));
                break;

            case 2:
                sEmotion.setText("Feeling Off");
                sEmoImg.setImageDrawable(getResources().getDrawable(R.drawable.emoji_2));
                break;

            case 3:
                sEmotion.setText("Feeling Ok");
                sEmoImg.setImageDrawable(getResources().getDrawable(R.drawable.emoji_3));
                break;

            case 4:
                sEmotion.setText("Feeling Pleasant");
                sEmoImg.setImageDrawable(getResources().getDrawable(R.drawable.emoji_4));
                break;

            case 5:
                sEmotion.setText("Feeling Great");
                sEmoImg.setImageDrawable(getResources().getDrawable(R.drawable.emoji_5));
                break;

        }



        dialogBuilder.setView(commentDetailPopup);
        dialog = dialogBuilder.create();
        //Needed this line to make the popup corners rounded
        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        dialog.show();

        sExitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });

    }

    private String CalculateTimeSincePosted(Timestamp t) {

        Date now = new Date();
        Date d = t.toDate();
        long elapsedTime = TimeUnit.MILLISECONDS.toMillis(now.getTime() - d.getTime());

        if (elapsedTime < 60000) {
            //elapsed time between 0 second - 59 sec
            return "Just now";
            //holder.uTime.setText(secTime + " seconds ago");
        } else if (elapsedTime >= 60000 && elapsedTime < 3600000) {
            // Elapsed time between 1 min - 59min
            long minTime = TimeUnit.MILLISECONDS.toMinutes(now.getTime() - d.getTime());
            if (minTime == 1) {
                return String.valueOf(minTime) + " minute ago";
            } else {
                return String.valueOf(minTime) + " minutes ago";
            }

            //holder.uTime.setText(minTime + " mins ago");
        } else {
            long hrTime = TimeUnit.MILLISECONDS.toHours(now.getTime() - d.getTime());
            int time = (int) hrTime;
            if (time >= 24) {

                long dayTime = TimeUnit.MILLISECONDS.toDays(now.getTime() - d.getTime());
                int days = (int) dayTime;
                if (days == 1) {
                    return String.valueOf(days) + " day ago";
                } else {
                    return String.valueOf(days) + " days ago";
                }

            } else if (time == 1) {
                return String.valueOf(hrTime) + " hr ago";
                //holder.uTime.setText(time + " hr ago");
            } else {
                return String.valueOf(hrTime) + " hrs ago";
                //holder.uTime.setText(time + " hrs ago");
            }
        }


    }



















    private class QuadrantDataEntry extends DataEntry {
        QuadrantDataEntry(Number x, Number y, String name) {
            setValue("x", x);
            setValue("y", y);
            setValue("name", name);
        }


    }

    private class Label extends DataEntry {
        Label(String anchor) {
            setValue("anchor", anchor);
        }

        Label(String anchor, Integer offsetX) {
            setValue("anchor", anchor);
            setValue("offsetX", offsetX);
        }

        Label(String anchor, Integer offsetX, Integer offsetY) {
            setValue("anchor", anchor);
            setValue("offsetX", offsetX);
            setValue("offsetY", offsetY);
        }
    }








    }


    /*
                        List<DataEntry> seriesData = new ArrayList<>();

                        for (StudentModel sm : sGraphList) {

                            Timestamp t = sm.getLatest_Timestamp();
                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMM HH:mm:ss");
                            String date = formatter.format(t.toDate());
                            seriesData.add(new QuadrantDataEntry(sm.getLatest_emotional_rating(), sm.getLatest_intensity_of_emotion(), sm.getFirstName()));
                            Log.d(TAG, "Added another entry");
                        }

                         */

