package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class TeacherDashActivity extends AppCompatActivity {



    public static final String TAG = "StudentDashActivity";
    BottomNavigationView bottomNavigationView;
    FirebaseAuth fAuth = FirebaseAuth.getInstance();

    Button logOutButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_teacher_dash);



        bottomNavigationView = findViewById(R.id.teacher_bottom_navigation);

        // On start always default to home screen
        getSupportFragmentManager().beginTransaction().replace(R.id.teacher_bottom_nav_container, new teacher_HomeFragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {

                    case R.id.nav_home:
                        fragment = new teacher_HomeFragment();
                        break;
                    case R.id.nav_student_list:
                        fragment = new teacher_studentListFragment();
                        break;
                    case R.id.nav_placeholder:
                        fragment = new teacher_placeholderFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new teacher_profileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.teacher_bottom_nav_container, fragment).commit();
                return true;
            }
        });






    }










}




// Logout button code
/*
        logOutButton = findViewById(R.id.teachDashLogOut);
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                FirebaseAuth.getInstance().signOut();
                Toast.makeText(TeacherDashActivity.this, "Logged out", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(TeacherDashActivity.this, MainActivity.class));
            }
        });

         */