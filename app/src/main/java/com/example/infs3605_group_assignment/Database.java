package com.example.infs3605_group_assignment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;

public class Database {

    private FirebaseAuth fAuth;
    private FirebaseFirestore db;

    private ArrayList<Model_CustomQuestion> customQuestions;

    public Database(FirebaseAuth fAuth, FirebaseFirestore db) {
        this.fAuth = fAuth;
        this.db = db;
    }

    public FirebaseAuth getfAuth() {
        return FirebaseAuth.getInstance();
    }

    public void setfAuth(FirebaseAuth fAuth) {
        this.fAuth = fAuth;
    }

    public FirebaseFirestore getDb() {
        return FirebaseFirestore.getInstance();
    }

    public void setDb(FirebaseFirestore db) {
        this.db = db;
    }

    //FirebaseAuth fAuth = FirebaseAuth.getInstance();
    //FirebaseFirestore db = FirebaseFirestore.getInstance();
}
