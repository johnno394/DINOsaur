package com.example.infs3605_group_assignment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
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

import java.util.ArrayList;

public class teacher_studentListFragment extends Fragment {


    RecyclerView rv;
    ArrayList<StudentModel> mListTeacher;
    teacherListAdapter mAdapter;
    StudentModel student;


    FirebaseFirestore db;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();
    public static final String TAG = "teacher_sListFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_teacher_student_list, container, false);


    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);


        rv = view.findViewById(R.id.rvStudentList);
        rv.setHasFixedSize(true);
        rv.setLayoutManager(new GridLayoutManager(this.getContext(), 3));

        db = FirebaseFirestore.getInstance();
        mListTeacher = new ArrayList<StudentModel>();
        //posible issue
        mAdapter = new teacherListAdapter(teacher_studentListFragment.this.getContext(), mListTeacher);

        rv.setAdapter(mAdapter);

        EventChangeListener();
        mAdapter.setOnItemClickListener(new studentListAdapter.onItemClickListener() {
            @Override
            public void onItemClick(int position) {


                student = mListTeacher.get(position);

                //Log.d("UID", student.getUid());
                //Log.d("position", String.valueOf(position));


                Intent intent = new Intent(getActivity(), teacher_studentListDetail.class);
                intent.putExtra("student Uid", student.getUid());
                startActivity(intent);


            }
        });




    }


    private void EventChangeListener() {

        Log.d(TAG, "Listener called");

        db.collection("Users")
                .whereEqualTo("isTeacher", "0")
                //.orderBy("FirstName", Query.Direction.DESCENDING)
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            Log.d(TAG, "Firestore error: " + error.getMessage());
                            return;
                        }
                        for (DocumentChange dc : value.getDocumentChanges()) {
                            if (dc.getType() == DocumentChange.Type.ADDED) {
                                mListTeacher.add(dc.getDocument().toObject(StudentModel.class));
                                Log.d(TAG, dc.getDocument().toObject(StudentModel.class).getFirstName());
                                Log.d(TAG, "Contains: " + mListTeacher.size());
                            }
                            mAdapter.notifyDataSetChanged();
                        }
                    }
                });


    }

}