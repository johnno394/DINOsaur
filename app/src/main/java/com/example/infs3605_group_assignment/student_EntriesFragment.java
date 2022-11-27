package com.example.infs3605_group_assignment;

import android.app.ProgressDialog;
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
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;


public class student_EntriesFragment extends Fragment {


    RecyclerView recyclerView;
    ArrayList<Model_WellbeingEntry> mList;
    studentEntryLogAdapter mAdapter;

    ProgressDialog progressDialog;

    FirebaseFirestore db;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public static final String TAG = "student_entriesFragment";

    CardView cvViewGraphs;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_student_entries, container, false);
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Loading dialog
        progressDialog = new ProgressDialog(this.getContext());
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Fetching Data...");
        progressDialog.show();


        recyclerView = view.findViewById(R.id.rvEntries2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this.getContext()));

        db = FirebaseFirestore.getInstance();
        mList = new ArrayList<Model_WellbeingEntry>();
        //posible issue
        mAdapter = new studentEntryLogAdapter(student_EntriesFragment.this.getContext(),mList);

        recyclerView.setAdapter(mAdapter);

        EventChangeListener();


        // CArdview button to view graphs
        cvViewGraphs = view.findViewById(R.id.cv_viewGraphs);

        cvViewGraphs.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), studentGraphActivity.class);
                startActivity(intent);
            }
        });





    }

    private void EventChangeListener() {

        db.collection("Users")
                .document(fAuth.getCurrentUser().getUid())
                .collection("Wellbeing_Log")
                .orderBy("Timestamp", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {

                        if (error != null) {

                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }

                        if (value.isEmpty()) {
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                            Toast.makeText(getContext(), "No logs to show!", Toast.LENGTH_SHORT).show();
                            return;
                        }

                        for (DocumentChange dc :value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                mList.add(dc.getDocument().toObject(Model_WellbeingEntry.class));
                            }
                            mAdapter.notifyDataSetChanged();
                            if (progressDialog.isShowing()) {
                                progressDialog.dismiss();
                            }
                        }

                    }
                });


    }


}