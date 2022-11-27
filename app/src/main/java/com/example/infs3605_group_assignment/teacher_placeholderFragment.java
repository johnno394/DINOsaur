package com.example.infs3605_group_assignment;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;


public class teacher_placeholderFragment extends Fragment {


    CardView btnCreateQuestion;
    static RecyclerView mRecyclerView;
    ArrayList<Model_CustomQuestion> mList;
    teacherCustomQuestionsAdapter mAdapter;

    FirebaseFirestore db;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    public static final String TAG = "Teacherplaceholder frag";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_placeholder, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        db = FirebaseFirestore.getInstance();

        mRecyclerView = view.findViewById(R.id.rvTeacherQuestions);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this.getContext(), LinearLayoutManager.VERTICAL,true));

        mList = new ArrayList<Model_CustomQuestion>();
        mAdapter = new teacherCustomQuestionsAdapter(teacher_placeholderFragment.this.getContext(),mList);
        mRecyclerView.setAdapter(mAdapter);
        EventChangeListener();

        mAdapter.setOnItemClickListener(new teacherCustomQuestionsAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(int position) {

                Log.d(TAG, "Position is: " + position);

                mList.get(position);
                Date d = mList.get(position).getPost_Date().toDate();
                Log.d(TAG, "Date is: " + d);

                SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
                String stringDate = sdf.format(d);
                Log.d(TAG, "Formatted date is: " + stringDate);

                Intent intent = new Intent(getActivity(), TeacherViewQuestionDetailActivity.class);
                intent.putExtra("postDate", stringDate);
                startActivity(intent);


            }
        });




        btnCreateQuestion = view.findViewById(R.id.cv_create_question);
        btnCreateQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), TeacherCreateQuestionActivity.class);
                startActivity(intent);
            }
        });


    }

    private void buildRecyclerView() {


    }

    private void EventChangeListener() {

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
                                //mList.add(0, dc.getDocument().toObject(Model_CustomQuestion.class));
                                mList.add(dc.getDocument().toObject(Model_CustomQuestion.class));
                            }
                            mAdapter.notifyDataSetChanged();

                        }

                        Log.d(TAG, "List count: " + mList.size());
                        mRecyclerView.scrollToPosition(mList.size()-1);

                    }
                });


    }
}