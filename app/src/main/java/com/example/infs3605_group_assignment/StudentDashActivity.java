package com.example.infs3605_group_assignment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.os.Bundle;
import android.view.MenuItem;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.navigation.NavigationBarView;

public class StudentDashActivity extends AppCompatActivity {

    public static final String TAG = "StudentDashActivity";

    BottomNavigationView bottomNavigationView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_dash);


        bottomNavigationView = findViewById(R.id.student_bottom_navigation);

        // On start always default to home screen
        getSupportFragmentManager().beginTransaction().replace(R.id.student_bottom_nav_container, new student_HomeFragment()).commit();

        bottomNavigationView.setSelectedItemId(R.id.nav_home);

        bottomNavigationView.setOnItemSelectedListener(new NavigationBarView.OnItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {

                Fragment fragment = null;

                switch (item.getItemId()) {

                    case R.id.nav_home:
                        fragment = new student_HomeFragment();
                        break;
                    case R.id.nav_entries:
                        fragment = new student_EntriesFragment();
                        break;
                    case R.id.nav_placeholder:
                        fragment = new student_PlaceholderFragment();
                        break;
                    case R.id.nav_profile:
                        fragment = new student_ProfileFragment();
                        break;
                }
                getSupportFragmentManager().beginTransaction().replace(R.id.student_bottom_nav_container, fragment).commit();
                return true;
            }
        });

    }

}