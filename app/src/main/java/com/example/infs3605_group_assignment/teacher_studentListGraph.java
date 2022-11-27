package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.anychart.AnyChart;
import com.anychart.AnyChartView;
import com.anychart.chart.common.dataentry.DataEntry;
import com.anychart.chart.common.dataentry.ValueDataEntry;
import com.anychart.charts.Cartesian;
import com.anychart.core.cartesian.series.Line;
import com.anychart.data.Mapping;
import com.anychart.data.Set;
import com.anychart.enums.Anchor;
import com.anychart.enums.MarkerType;
import com.anychart.enums.TooltipPositionMode;
import com.anychart.graphics.vector.Stroke;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class teacher_studentListGraph extends AppCompatActivity {

    public static final String TAG = "teacher_studentGraph";
    List<Model_WellbeingEntry> mList = new ArrayList<Model_WellbeingEntry>();

    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    String studentUid;
    ImageView backBtn;
    ProgressDialog progressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_student_list_graph);


        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Graph Data...");
        progressDialog.show();

        Intent intent= getIntent();
        studentUid = intent.getStringExtra("UID");


        backBtn = findViewById(R.id.backbtn);

        backBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(teacher_studentListGraph.this, teacher_studentListDetail.class);
                i.putExtra("student Uid", studentUid);
                startActivity(i);

            }
        });



        // Gets users wellbeing logs
        db.collection("Users")
                .document(studentUid)
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.ASCENDING)
                .limitToLast(5)
                .get()
                .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                    @Override
                    public void onSuccess(QuerySnapshot queryDocumentSnapshots) {

                        List<DocumentSnapshot> q = queryDocumentSnapshots.getDocuments();


                        for (DocumentSnapshot ds : q) {
                            mList.add(ds.toObject(Model_WellbeingEntry.class));
                            Log.d(TAG, "Adding ");
                        }

                        Log.d(TAG, "Success ");

                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.d(TAG, "Fail ");
                    }
                })
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        AnyChartView anyChartView = findViewById(R.id.studentChart);
                        //anyChartView.setProgressBar(findViewById(R.id.progress_bar));


                        Cartesian cartesian = AnyChart.line();

                        cartesian.animation(true);

                        cartesian.padding(10d, 20d, 5d, 20d);

                        cartesian.crosshair().enabled(true);
                        cartesian.crosshair()
                                .yLabel(true)
                                // TODO ystroke
                                .yStroke((Stroke) null, null, null, (String) null, (String) null);

                        cartesian.tooltip().positionMode(TooltipPositionMode.POINT);

                        // Set bound limits for y scale
                        cartesian.yScale().minimum(0);
                        cartesian.yScale().maximum(5);

                        //cartesian.title("Trend of Sales of the Most Popular Products of ACME Corp.");

                        cartesian.yAxis(0).title("Emotional Scores");

                        cartesian.xAxis(0).labels().padding(5d, 5d, 5d, 5d);

                        List<DataEntry> seriesData = new ArrayList<>();


                        Log.d(TAG, "Accessing list of size: " + mList.size());
                        for (Model_WellbeingEntry wm : mList) {
                            Timestamp t = wm.getTimestamp();

                            SimpleDateFormat formatter = new SimpleDateFormat("ddMMM HH:mm:ss");
                            String date = formatter.format(t.toDate());
                            seriesData.add(new teacher_studentListGraph.CustomDataEntry(date, wm.getEmotional_rating().doubleValue(), wm.getIntensity_of_emotion().doubleValue()));
                            Log.d(TAG, "Added another entry");
                        }

                        if (seriesData.isEmpty()) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getApplicationContext(), "No logs to graph!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        Set set = Set.instantiate();
                        set.data(seriesData);
                        Mapping series1Mapping = set.mapAs("{ x: 'x', value: 'value' }");
                        Mapping series2Mapping = set.mapAs("{ x: 'x', value: 'value2' }");
                        //Mapping series3Mapping = set.mapAs("{ x: 'x', value: 'value3' }");

                        Line series1 = cartesian.line(series1Mapping);
                        series1.name("Emotional Score");
                        series1.hovered().markers().enabled(true);
                        series1.hovered().markers()
                                .type(MarkerType.CIRCLE)
                                .size(4d);
                        series1.tooltip()
                                .position("right")
                                .anchor(Anchor.LEFT_CENTER)
                                .offsetX(5d)
                                .offsetY(5d);


                        Line series2 = cartesian.line(series2Mapping);
                        series2.name("Emotional Intensity");
                        series2.hovered().markers().enabled(true);
                        series2.hovered().markers()
                                .type(MarkerType.CIRCLE)
                                .size(4d);
                        series2.tooltip()
                                .position("right")
                                .anchor(Anchor.LEFT_CENTER)
                                .offsetX(5d)
                                .offsetY(5d);


                        cartesian.legend().enabled(true);
                        cartesian.legend().fontSize(13d);
                        cartesian.legend().padding(0d, 0d, 10d, 0d);

                        anyChartView.setChart(cartesian);

                        final Handler handler = new Handler();
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if (progressDialog.isShowing()) {
                                    progressDialog.dismiss();
                                }
                            }
                        }, 5000);
                    }
                });



    }

    private class CustomDataEntry extends ValueDataEntry {

        CustomDataEntry(String x, double value, double value2) {
            super(x, value);
            setValue("value2", value2);

        }

    }
}